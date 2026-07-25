package com.smartcity.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for sending email notifications in the Smart City Guide application.
 */
public class EmailService {
    private static final ExecutorService EMAIL_EXECUTOR = new ThreadPoolExecutor(
        5,
        10,
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(100)
    );
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final String SMTP_HOST = getEnv("SMTP_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = getEnv("SMTP_PORT", "587");
    private static final String SMTP_USER = getEnv("SMTP_USER", "");
    private static final String SMTP_PASSWORD = getEnv("SMTP_PASSWORD", "");
    private static final String SMTP_FROM = getEnv("SMTP_FROM", "");
    private static final String APP_URL = initAppUrl();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownExecutor();
        }, "email-executor-shutdown-hook"));
    }

    private static String initAppUrl() {
        String url = System.getenv("APP_URL");
        if (url == null || url.isBlank()) {
            LOGGER.info("APP_URL environment variable is not set. Defaulting to local development URL: http://localhost:5000");
            return "http://localhost:5000";
        }
        return url;
    }

    /**
     * Shuts down the email executor service cleanly, waiting for pending tasks to complete.
     */
    public static void shutdownExecutor() {
        if (!EMAIL_EXECUTOR.isShutdown()) {
            LOGGER.info("Shutting down EmailService executor...");
            EMAIL_EXECUTOR.shutdown();
            try {
                if (!EMAIL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    EMAIL_EXECUTOR.shutdownNow();
                    if (!EMAIL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.log(Level.WARNING, "EmailService executor did not terminate cleanly.");
                    }
                }
            } catch (InterruptedException ie) {
                EMAIL_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Reads environment variable or returns default value if missing.
     *
     * @param key          Environment variable key.
     * @param defaultValue Default value if key is not set or empty.
     * @return The environment variable value or default value.
     */
    public static String getEnv(String key, String defaultValue) {
        String env = System.getenv(key);
        return (env != null && !env.isBlank()) ? env : defaultValue;
    }

    /**
     * Checks if SMTP settings are fully configured.
     *
     * @return true if valid SMTP credentials are available, false otherwise.
     */
    public static boolean isSmtpConfigured() {
        return !SMTP_USER.isBlank() && !SMTP_PASSWORD.isBlank() && !SMTP_FROM.isBlank();
    }

    /**
     * Loads the welcome email HTML template and injects the username.
     *
     * @param username The registered username.
     * @return Formatted HTML email content.
     */
    private static String loadWelcomeEmailTemplate(String username) {
        String templateContent = "";
        try {
            Path path = Paths.get("templates/welcome_email.html");
            if (Files.exists(path)) {
                templateContent = Files.readString(path, StandardCharsets.UTF_8);
            } else {
                try (InputStream is = EmailService.class.getResourceAsStream("/templates/welcome_email.html")) {
                    if (is != null) {
                        templateContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load welcome email template file", e);
        }

        if (templateContent.isBlank()) {
            templateContent = "<!DOCTYPE html><html><body>"
                    + "<h2>Welcome to Smart City Guide, {{username}}!</h2>"
                    + "<p>Thank you for registering.</p>"
                    + "</body></html>";
        }

        return templateContent.replace("{{username}}", username)
                .replace("{{appUrl}}", APP_URL);
    }

    /**
     * Sends a welcome email to the user upon registration if SMTP configuration is valid.
     *
     * @param toEmail  Destination email address.
     * @param username Registered username.
     */
    public static void sendWelcomeEmail(String toEmail, String username) {


        CompletableFuture.runAsync(() ->
        {
            sendEmail(toEmail, username);
        }, EMAIL_EXECUTOR).exceptionally(ex -> {
            LOGGER.log(Level.SEVERE, "Failed to send welcome email to " + toEmail, ex);
            return null;
        })
        ;
    }

    private static void sendEmail(String toEmail, String username)
    {
        if (!isSmtpConfigured()) {
            System.out.println("⚠️ SMTP credentials not fully configured (SMTP_USER/SMTP_PASSWORD/SMTP_FROM). "
                + "Skipping welcome email.");
            return;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Welcome to Smart City Guide!");

            String htmlContent = loadWelcomeEmailTemplate(username);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            System.out.println("Sending welcome email to " + toEmail + "...");
            Transport.send(message);
            System.out.println("Welcome email sent successfully!");
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send welcome email to " + toEmail, e);
        }
    }
}


