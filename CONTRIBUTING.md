# 🤝 Contributing to Smart City Guide

Thank you for your interest in contributing to **Smart City Guide**! 🎉  
Whether you're fixing a bug, adding a feature, or improving documentation — all contributions are welcome.

---

## 🎓 What You'll Learn

By contributing to Smart City Guide, you'll gain hands-on experience with:

| Skill | Where You'll Use It |
|-------|-------------------|
| ☕ Core Java | Writing methods, classes, control flow |
| 🗄️ MySQL & JDBC | Querying databases, prepared statements |
| 🔐 Input Validation | Regex, defensive programming |
| 🏗️ OOP Principles | Encapsulation, getters/setters |
| 🔀 Git Workflow | Fork, branch, commit, pull request |
| 🐛 Debugging | Reading stack traces, fixing bugs |
| 📝 Documentation | Writing clear Javadoc and comments |
| 📦 Maven | Building the project and managing dependencies in `pom.xml` |
| ✅ Code Style Checks | Keeping the Checkstyle rules in `checkstyle.xml` happy |

---

## 📋 Table of Contents

- [What You'll Learn](#-what-youll-learn)
- [Code of Conduct](#-code-of-conduct)
- [Getting Started](#-getting-started)
- [How to Contribute](#-how-to-contribute)
  - [Reporting Bugs](#-reporting-bugs)
  - [Suggesting Features](#-suggesting-features)
  - [Submitting Code Changes](#-submitting-code-changes)
- [Development Setup](#-development-setup)
- [Coding Standards](#-coding-standards)
- [Commit Message Guidelines](#-commit-message-guidelines)
- [Pull Request Guidelines](#-pull-request-guidelines)
- [Good First Issues](#-good-first-issues)

---

## 📜 Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md) and maintain a respectful and inclusive environment for everyone. Please be kind, constructive, and welcoming — this is a beginner-friendly project.

---

## 🚀 Getting Started

1. **Fork** the repository by clicking the **Fork** button at the top-right of the [repository page](https://github.com/Rajath2005/SmartCityApp).

2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/SmartCityApp.git
   cd SmartCityApp
   ```

3. **Add the upstream remote** so you can sync with the original repository:
   ```bash
   git remote add upstream https://github.com/Rajath2005/SmartCityApp.git
   ```

4. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
   Use a descriptive name like `feature/add-search-filter` or `fix/login-bug`.

---

## 🛠️ How to Contribute

### 🐛 Reporting Bugs

Found a bug? Please [open an issue](https://github.com/Rajath2005/SmartCityApp/issues) with the following details:

- A clear, descriptive title
- Steps to reproduce the bug
- Expected behaviour vs. actual behaviour
- Your Java version (`java -version`), MySQL version, and operating system
- Any relevant error messages or stack traces

### 💡 Suggesting Features

Have an idea? [Open an issue](https://github.com/Rajath2005/SmartCityApp/issues) with the `enhancement` label and include:

- A clear description of the feature
- The problem it solves or the value it adds
- Any implementation ideas you may have

### 📝 Submitting Code Changes

1. Make sure an issue exists for the change you want to make (or create one first).
2. Follow the [Development Setup](#-development-setup) steps.
3. Write clean, well-commented code and test it locally.
4. Commit your changes following the [Commit Message Guidelines](#-commit-message-guidelines).
5. Push your branch and [open a Pull Request](#-pull-request-guidelines).

---

## 💻 Development Setup

### Prerequisites

- **Java JDK 8 or higher** — [Download here](https://www.oracle.com/java/technologies/downloads/)
- **MySQL Server** — [Download here](https://dev.mysql.com/downloads/installer/)
- A terminal or an IDE (IntelliJ IDEA, Eclipse, or VS Code)
- **MySQL JDBC Driver** — Ensure `mysql-connector-java.jar` is in your project's classpath.

Verify your Java and MySQL installations:
```bash
java -version
mysql --version
```

### Database Setup

Before running the application, you must initialize the MySQL database:

1. Open your terminal or MySQL client.
2. Run the provided SQL setup script:
   ```bash
   mysql -u root -p < db_setup.sql
   ```
3. This will create the `smart_city_guide` database, the `users` table, and the `places` table, and it will insert a default admin user.

### Build & Run

```bash
# Navigate into the source directory
cd src

# Compile the application (Ensure your classpath includes the MySQL JDBC driver)
javac com/smartcity/main/SmartCityApp.java

# Run the application
java com.smartcity.main.SmartCityApp
```

> 💡 Using an IDE? Import the project, add the MySQL connector to your project structure/dependencies, and run `SmartCityApp.java` directly — no terminal needed.

### Keeping Your Fork Up to Date

Before starting new work, sync with the upstream repository:
```bash
git fetch upstream
git checkout main
git merge upstream/main
```

---

## 🎨 Coding Standards

- Follow standard **Java naming conventions**:
  - `PascalCase` for class names (e.g., `SmartCityApp`)
  - `camelCase` for method and variable names (e.g., `findPlaceByCategory`)
  - `UPPER_SNAKE_CASE` for constants (e.g., `MAX_RETRIES`)
- Keep methods short and focused on a single responsibility.
- Add Javadoc comments to all public classes and methods.
- Use meaningful variable names — avoid single-letter names except in loops.
- Remove unused imports, variables, and dead code before submitting.
- Maintain consistent indentation (4 spaces, no tabs).
- **Security Check:** Never hardcode production passwords. Use parameterized queries (PreparedStatements) for all SQL logic.

---

## ✍️ Commit Message Guidelines

Write clear, concise commit messages. Use the following format:

```
<emoji> <type>: <short description>
```

| Emoji | Type       | Use for                              |
|-------|------------|--------------------------------------|
| ✨    | `Add`      | New features or files                |
| 🐛    | `Fix`      | Bug fixes                            |
| ♻️    | `Refactor` | Code refactoring without behaviour change |
| 📝    | `Docs`     | Documentation updates                |
| 🧹    | `Chore`    | Maintenance, cleanup, or dependency updates |
| 🧪    | `Test`     | Adding or updating tests             |

**Examples:**
```
✨ Add: search places by rating
🐛 Fix: null pointer in login flow
📝 Docs: update README database setup instructions
```

---

## 🔀 Pull Request Guidelines

- Target the `main` branch for all pull requests.
- Keep PRs focused — one feature or fix per PR.
- Fill in the pull request template completely (title, description, linked issue).
- Reference the related issue in the PR description (e.g., `Closes #42` or `Fixes #23`).
- Make sure the code compiles and runs without errors (including database interactions) before submitting.
- Respond promptly to review feedback and push updates to the same branch.

---

## 🌱 Good First Issues

New to open source? Look for issues tagged [`good first issue`](https://github.com/Rajath2005/SmartCityApp/issues?q=label%3A%22good+first+issue%22) — they are specifically selected to be beginner-friendly entry points into the codebase.

> 💡 **Claiming an issue:** Found one you want to work on? Comment `/assign` or `/take` on it and our bot will assign it to you automatically (as long as it isn't already claimed).

**Current Focus Areas for Beginners:**
If you are looking for things to build, we highly recommend looking into these architectural improvements:
1.  **Migrating to DAOs:** Currently, `SmartCityApp.java` handles all SQL queries. Refactoring this logic into separate Data Access Objects (e.g., `UserDAO.java`, `PlaceDAO.java`) is a great way to learn Layered Architecture!
2.  **Resource Leaks (See Issue #23):** Implementing Java's `try-with-resources` blocks to safely manage JDBC Connections and PreparedStatements.
3.  **Password Security:** The application currently stores passwords in plain text. Implementing a hashing algorithm like BCrypt would be a massive improvement.

---

## 📄 License

By contributing to Smart City Guide, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

<div align="center">

**Made with ❤️ by [Rajath2005](https://github.com/Rajath2005) and the community**

</div>
