# Employee Leave Management System

A desktop application built using **Java (Swing/AWT)**, **JDBC**, and **Oracle Database** for college DBMS demonstrations and viva presentations.

---

## 📁 Project Directory Structure

```text
Employee Leave Management System/
│
├── src/
│   ├── Main.java               # Main entry point for the desktop GUI
│   ├── DBConnection.java       # Oracle JDBC connection manager
│   ├── Login.java              # Role-based login (Employee / Manager)
│   ├── EmployeeDashboard.java  # Employee portal (live balances, quick links)
│   ├── ApplyLeave.java         # Leave application with date & quota validation
│   ├── LeaveHistory.java       # Status tracking of historical applications
│   └── ManagerDashboard.java   # Manager portal with ACID transaction approvals
│
├── database/
│   ├── create_tables.sql       # Table schema DDL, constraints, sequences
│   └── sample_data.sql         # Test dataset (2 managers, 4 employees, 3 leave types)
│
├── run.sh                      # One-click startup script for macOS/Linux
├── run.bat                     # One-click startup script for Windows
└── README.md                   # Complete documentation and setup guide
```

---

## 🚀 Quick Setup Instructions

### 1. Database Setup (Oracle Database / SQL Developer)
1. Open **Oracle SQL Developer** or **SQL*Plus**.
2. Run `database/create_tables.sql` (press **F5** in SQL Developer to run as script).
3. Run `database/sample_data.sql` (press **F5**).

### 2. Configure Database Connection
Open `src/DBConnection.java` and make sure the connection settings match your Oracle instance:
```java
private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
private static final String USERNAME = "system"; // your Oracle username
private static final String PASSWORD = "your_password"; // your Oracle password
```

### 3. Add `ojdbc8.jar`
Place the Oracle JDBC driver file (`ojdbc8.jar`) in the project root or in a `lib/` folder.

### 4. Run the Project
* **macOS / Linux**:
  ```bash
  ./run.sh
  ```
* **Windows**:
  Double-click `run.bat` or run:
  ```cmd
  run.bat
  ```

---

## 🔑 Pre-Configured Test Logins

| Role | Name | Email | Password | Manager |
| :--- | :--- | :--- | :--- | :--- |
| **Manager** | Alice Johnson | `alice@company.com` | `alice123` | - |
| **Manager** | Bob Williams | `bob@company.com` | `bob123` | - |
| **Employee** | John Doe | `john@company.com` | `john123` | Alice Johnson (Mgr 1) |
| **Employee** | Jane Smith | `jane@company.com` | `jane123` | Alice Johnson (Mgr 1) |
| **Employee** | Michael Brown | `michael@company.com` | `michael123` | Bob Williams (Mgr 2) |
| **Employee** | Emily Davis | `emily@company.com` | `emily123` | Bob Williams (Mgr 2) |

---

## ⚙️ Key Technical Features
1. **Third Normal Form (3NF) Database Design**: Eliminates all partial and transitive functional dependencies.
2. **ACID Transactions in Approvals**: Guarantees that leave approval status change and employee balance deduction happen atomically using `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()`.
3. **Prepared Statements**: Prevents SQL Injection attacks on all operations.
4. **Client-Side & Server-Side Validations**: Date validation, remaining quota checks, duplicate processing locks.
