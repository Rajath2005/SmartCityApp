ALTER TABLE users
    ADD COLUMN email VARCHAR(255);

UPDATE users
SET email = CONCAT(username, '@example.com')
WHERE email IS NULL;

ALTER TABLE users
    MODIFY COLUMN email VARCHAR(255) NOT NULL,
    ADD UNIQUE (email);