# Smart City Guide

A console-based Java CLI application that lets users browse, search, and navigate city attractions. Admins can add, update, and delete places from a MySQL database.

## Stack

- **Language:** Java 21 (OpenJDK)
- **Build:** Maven (produces `target/app.jar`)
- **Database:** MySQL 8.4 (runs locally inside the Replit container)
- **Auth:** SHA-256 password hashing

## How to run

The workflow `Start application` handles everything:

1. Initializes the MySQL data directory on first run (`~/.mysql/smart_city_data`)
2. Starts the MySQL server (socket at `~/.mysql/mysql.sock`)
3. Runs `db_setup.sql` to create the database and tables (idempotent — safe to re-run)
4. Launches the Java CLI app

**Manual build:**
```bash
JAVA_HOME=/nix/store/3ilfkn8kxd9f6g5hgr0wpbnhghs4mq2m-openjdk-21.0.7+6 \
  PATH=$JAVA_HOME/bin:$PATH \
  mvn package -DskipTests
```

**Run directly:**
```bash
bash start.sh
```

## Environment variables (all optional)

| Variable      | Default          | Description                  |
|---------------|------------------|------------------------------|
| `DB_HOST`     | `127.0.0.1`      | MySQL host                   |
| `DB_PORT`     | `3306`           | MySQL port                   |
| `DB_NAME`     | `smart_city_guide` | Database name              |
| `DB_USER`     | `root`           | MySQL user (bootstrap always uses root; keep as `root` for full access) |
| `DB_PASSWORD` | *(empty)*        | MySQL password               |

## Default admin credentials

- **Username:** `admin`
- **Password:** `Admin@123`

## Project structure

```
src/com/smartcity/
  main/SmartCityApp.java   # CLI entry point, all menus and SQL queries
  db/DBConnection.java     # MySQL connection via env vars
  model/Place.java         # Place model
  model/User.java          # User model
db_setup.sql               # Schema (used by start.sh on first run)
pom.xml                    # Maven build (Java 21, mysql-connector-j 9.1.0)
start.sh                   # Startup script: MySQL init + app launch
```

## User preferences

- Keep existing project structure (Java/Maven/MySQL) as-is.
