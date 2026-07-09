<div align="center">

<!-- Banner -->
<img src="Assets/SmartCity.drawio.png" alt="Smart City Guide Banner" width="100%"/>

<br/>

# 🏙️ Smart City Guide

### *Your intelligent companion to explore, navigate, and discover the city*

<br/>

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![CLI](https://img.shields.io/badge/Interface-CLI-blue?style=for-the-badge&logo=windowsterminal&logoColor=white)]()
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge&logo=github)](https://github.com/Rajath2005/SmartCityApp/pulls)
[![Contributors](https://img.shields.io/github/contributors/Rajath2005/SmartCityApp?style=for-the-badge&color=orange)](https://github.com/Rajath2005/SmartCityApp/graphs/contributors)

<br/>

[🚀 Get Started](#-getting-started) &nbsp;|&nbsp;
[✨ Features](#-features) &nbsp;|&nbsp;
[🏗️ Architecture](#️-architecture--project-structure) &nbsp;|&nbsp;
[🤝 Contributing](#-contributing) &nbsp;|&nbsp;
[🗺️ Roadmap](#️-roadmap)

</div>

---

## 📌 Overview

**Smart City Guide** is an interactive, console-based Java application that helps residents and tourists **discover, search, and navigate city attractions** with ease. It acts as an intelligent city companion with a complete role-based system for both regular users and administrators.

Whether you're looking for the best restaurant downtown or managing the city's attraction database as an admin — this app has you covered.

> ⚠️ **Note:** The application uses a **MySQL Database** for persistent storage. You will need to set up a local database before running the app. See the [Database Setup](#-database-setup) section below.

---

## ✨ Features

### 👤 User Features

| Feature | Description |
|---|---|
| 🔐 **Register & Login** | Secure account creation and authentication. |
| 🗺️ **View Attractions** | Browse a curated list of city places. |
| 🔍 **Search by Category** | Find places by type — Restaurant, Park, Hotel, Museum. |
| 📍 **Search by Location** | Find places in specific areas like Downtown or Main Street. |
| 🚗 **Navigation** | Simulated access to directions and nearby services. |

---

### 🛠️ Admin Features

| Feature | Description |
|---|---|
| ➕ **Add Place** | Add new city attractions to the database. |
| ✏️ **Update Place** | Edit details of existing places. |
| ❌ **Delete Place** | Remove outdated or incorrect entries. |
| 📊 **System Monitoring** | View system logs and user activity (Simulated). |

---

## 🏗️ Architecture & Project Structure

The application currently follows a monolithic architecture built around a centralized controller (`SmartCityApp.java`), making it an excellent starting point for beginners to understand Java control flows and JDBC. We are actively looking for contributors to help us refactor this into a Layered Architecture (MVC).

```
SmartCityGuide/
│
├── 📁 Assets/
│   └── SmartCity.drawio.png        # Architecture diagram
│
├── 📁 src/
│   └── com/
│       └── smartcity/
│           ├── 📁 main/
│           │   └── SmartCityApp.java    # Main entry point, controller & UI logic
│           ├── 📁 model/
│           │   ├── Place.java           # City place data model (POJO)
│           │   └── User.java            # User data model & roles (POJO)
│           └── 📁 db/
│               └── DBConnection.java    # JDBC MySQL Connection Manager
│
├── db_setup.sql                    # SQL script to initialize the database
├── README.md
├── CONTRIBUTING.md                 # Contribution guidelines
├── CODE_OF_CONDUCT.md              # Community standards
└── .gitignore
```

---

## 🚀 Getting Started

### ✅ Prerequisites

Make sure you have the following installed:

- ☕ **Java JDK 8 or higher** → [Download here](https://www.oracle.com/java/technologies/downloads/)
- 🐬 **MySQL Server** → [Download here](https://dev.mysql.com/downloads/installer/)
- 💻 **Terminal / IDE** → VS Code, IntelliJ IDEA, or Eclipse
- 📦 **MySQL JDBC Driver** → Ensure `mysql-connector-java.jar` is in your project's classpath.

---

### 💾 Database Setup

Before running the application, you must initialize the MySQL database.

1. Open your MySQL client (e.g., MySQL Workbench or CLI).
2. Run the provided SQL script:
   ```bash
   mysql -u root -p < db_setup.sql
   ```
   *(This creates the `smart_city_guide` database and the `users` and `places` tables).*

---

### ⚡ Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/Rajath2005/SmartCityApp.git

# 2. Navigate into the project
cd SmartCityApp/src

# 3. Compile the application (Make sure to include the MySQL JDBC Driver in your classpath)
javac com/smartcity/main/SmartCityApp.java

# 4. Run the application
java com.smartcity.main.SmartCityApp
```

> 💡 **Tip for beginners:** If you're using an IDE like IntelliJ or Eclipse, import the project and run `SmartCityApp.java` directly. Ensure the MySQL connector library is added to your project structure.

---

### 🐳 Run with Docker

No local Java or MySQL install needed — everything runs in containers.

**Prerequisites:** Docker + Docker Compose.

```bash
# 1. Clone the repository
git clone https://github.com/Rajath2005/SmartCityApp.git
cd SmartCityApp

# 2. Build and start the app (attached to your terminal, since it's a CLI app)
docker compose run --rm app
```

This spins up a MySQL container (auto-seeded with `db_setup.sql`) and runs the app against it. `docker compose run` keeps stdin/stdout attached so menu prompts work interactively.

**Useful commands:**

```bash
# Start just the database in the background
docker compose up -d mysql-db

# Rebuild the app image after code changes
docker compose build app

# Reset the database (wipes all data, re-seeds on next run)
docker compose down -v

# Stop everything
docker compose down
```

Default DB credentials (override via `DB_PASSWORD` env var before running compose): user `root`, password `root`, database `smart_city_guide`.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java ☕ |
| **Interface** | Command Line Interface (CLI) |
| **Data Storage** | MySQL Database 🐬 |
| **Architecture** | Monolithic (Role-Based) |

---

## 🤝 Contributing

We ❤️ contributions — whether you're fixing a bug, adding a feature, or improving docs! This project is highly focused on being a welcoming space for beginner Java developers.

### 🌱 Ready to Contribute?

Please read our [**Contributing Guidelines**](CONTRIBUTING.md) to learn how to get started. 
We have specifically outlined **Good First Issues** (like refactoring to DAOs and fixing resource leaks) in the `CONTRIBUTING.md` file.

Please also review our [**Code of Conduct**](CODE_OF_CONDUCT.md) before participating.

---

## 👥 Contributors

A huge shoutout to everyone who has contributed to this project! 🙌

<!-- readme: contributors -start -->
<table>
	<tbody>
		<tr>
            <td align="center">
                <a href="https://github.com/Rajath2005">
                    <img src="https://avatars.githubusercontent.com/u/168326104?v=4" width="100;" alt="Rajath2005"/>
                    <br />
                    <sub><b>Rajath Kiran A</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/alexanderliberacion-cmd">
                    <img src="https://avatars.githubusercontent.com/u/244034238?v=4" width="100;" alt="alexanderliberacion-cmd"/>
                    <br />
                    <sub><b>alexanderliberacion-cmd</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/cordeirops">
                    <img src="https://avatars.githubusercontent.com/u/13083210?v=4" width="100;" alt="cordeirops"/>
                    <br />
                    <sub><b>Pedro Sbaraini Cordeiro</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/shikha033">
                    <img src="https://avatars.githubusercontent.com/u/177534265?v=4" width="100;" alt="shikha033"/>
                    <br />
                    <sub><b>Shikha Singh</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/sshrrutiiii">
                    <img src="https://avatars.githubusercontent.com/u/196079073?v=4" width="100;" alt="sshrrutiiii"/>
                    <br />
                    <sub><b>Shruti Dixit</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/rajibul004">
                    <img src="https://avatars.githubusercontent.com/u/157000457?v=4" width="100;" alt="rajibul004"/>
                    <br />
                    <sub><b>Rajibul Mondal</b></sub>
                </a>
            </td>
		</tr>
		<tr>
            <td align="center">
                <a href="https://github.com/JhansiOruganti-43">
                    <img src="https://avatars.githubusercontent.com/u/155613006?v=4" width="100;" alt="JhansiOruganti-43"/>
                    <br />
                    <sub><b>Jhansi Oruganti</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/Julito-Dev">
                    <img src="https://avatars.githubusercontent.com/u/210993135?v=4" width="100;" alt="Julito-Dev"/>
                    <br />
                    <sub><b>Julito-Dev</b></sub>
                </a>
            </td>
            <td align="center">
                <a href="https://github.com/Nishcahy">
                    <img src="https://avatars.githubusercontent.com/u/141355948?v=4" width="100;" alt="Nishcahy"/>
                    <br />
                    <sub><b>Nishchay</b></sub>
                </a>
            </td>
		</tr>
	<tbody>
</table>
<!-- readme: contributors -end -->

*Want to see your face here? Check out our [Contribution guide](CONTRIBUTING.md)!*

---

## 🗺️ Roadmap

Here's what's coming next. Check our [Issues tab](https://github.com/Rajath2005/SmartCityApp/issues) to claim one!

- [ ] 🏗️ **Architecture Refactor** — Migrate SQL logic from `SmartCityApp` to `DAO` (Data Access Object) classes.
- [ ] 🔐 **Security** — Hash user passwords (e.g., BCrypt).
- [ ] 🌐 **REST API** — Expose features via a Spring Boot REST API.
- [ ] 🖥️ **GUI Interface** — Build a JavaFX or Swing-based graphical UI.
- [ ] ⭐ **Ratings & Reviews** — Let users rate and review places.
- [ ] 🧪 **Unit Tests** — Add JUnit tests for all core classes.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ by [Rajath2005](https://github.com/Rajath2005) and the community**

*Part of the Creative Coding Progress Series*

<br/>

[![Back to Top](https://img.shields.io/badge/Back%20to%20Top-%E2%AC%86-blue?style=for-the-badge)](#️-smart-city-guide)

</div>
