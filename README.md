# 🛡️ Cyber Immune System: Database Resilience PoC

**Project Title:** `Cyber Immune System 1.0v - Created by waruna.dilshan`

## ⚠️ Educational Disclaimer

**This project is created strictly for educational and research purposes only.**

The "Hacker Window" and simulated attack features are designed solely to demonstrate **Database Failover** and **System Resilience** concepts in a controlled environment. The source code **must not** be used for any illegal, unethical, or malicious activities. The developer and contributors hold no responsibility for any misuse of this code.

**Intended Audience:** Software Engineering Students, Cybersecurity Researchers, and Developers learning about High Availability systems.

---

### 📝 Project Overview

This is a Proof-of-Concept (PoC) application built in Java Swing to demonstrate **database resilience** and a **self-healing failover mechanism** against simulated database failures (simulating a cyber attack).

The core of this system is the intelligent `DBHandler` which ensures application continuity by seamlessly switching from a primary database (`user_db`) to a redundant backup database (`user_backup_db`) when the primary one fails. This showcases a critical principle in **High Availability (HA)** and **Disaster Recovery (DR)**, essential for robust cyber defense architecture.

### 💡 Core Security & Engineering Concept

* **Cyber Resilience:** The application maintains operational service (read/write functionality) even when the primary data source is compromised or offline, effectively preventing a **Denial of Service (DoS)** to the user data layer.
* **Failover Logic:** The `DBHandler` attempts to connect/write to the Primary DB first. If an exception (simulated attack) is caught, it automatically reroutes the operation to the Backup DB, a process often referred to as **Automatic Healing**.
* **Data Integrity:** The application attempts to keep both the Primary and Backup databases synchronized (writing to both when the Primary is healthy) to minimize data loss upon a failover event.

### 💻 Technology Stack

| Category | Technology |
| :--- | :--- |
| **Frontend/GUI** | Java Swing (AWT/Swing for desktop application) |
| **Backend/Logic** | Pure Java (JDK 8+) |
| **Database** | MySQL (Simulating two distinct database instances) |
| **Theming** | FlatLaf (for modern look and feel) |
| **Connectivity** | JDBC (MySQL Connector/J) |

### ▶️ Getting Started (Prerequisites)

To run this project locally, ensure you have the following installed and configured:

1.  **Java Development Kit (JDK 8 or higher)**
2.  **MySQL Server:** A running instance of MySQL.
3.  **MySQL Connector/J:** The JDBC driver for Java, which must be included in your project's classpath.

### ⚙️ Database Configuration

Two databases must be created on your local MySQL server:

1.  `user_db` (Primary)
2.  `user_backup_db` (Backup)

The following SQL script should be executed on **both** databases:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL
);
⚠️ Important: Update the database password in Model/DBHandler.java:

Java

// DBHandler.java
private final String DB_PASS = "YOUR_DB_PASSWORD"; // Change this to your actual MySQL password
🚀 How to Test the Resilience
Run the HomeScreen.java file.

Test 1 (Normal State): Click 'Check Connection' (Primary) and use 'SIGNUP'. Both operations should confirm connection to the Primary DB.

Simulate Attack: Click the 'Open Hacker Window' button, and then click 'Attack to Company Database'. This sets DBHandler.isPrimaryFailing = true;.

Test 2 (Healed State):

Click 'Check Connection' (Primary). It will show ❌ ERROR! Database is DOWN.

Attempt a 'SIGNUP' (Write operation). The system will automatically fall back, and the console will show ✅ HEALED! User registered successfully via Backup DB.

Attempt 'Get User' (Read operation). The console will show ✅ HEALED! Data read successfully from Backup DB.

🤝 Contribution
This is a personal educational project. However, suggestions, bug reports, and pull requests are welcome.

👨‍💻 Author
Waruna Dilshan

Role: Software Engineer Student / Cybersecurity Enthusiast

Connect: www.linkedin.com/in/waruna-dilshan-3ba510389
