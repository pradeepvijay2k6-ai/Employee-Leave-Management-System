# Employee Leave Management System

A desktop application built using **Java (Swing/AWT)**, **JDBC**, and **Oracle Database** for college DBMS demonstrations and viva presentations.

---

## 📁 Project Directory Structure

```text
Employee Leave Management System/
│
├── src/
│   ├── Main.java               # Main application entry point
│   ├── DBConnection.java       # Oracle JDBC connection manager
│   ├── Login.java              # Role-based login (Employee / Manager)
│   ├── EmployeeDashboard.java  # Employee portal (live balances, quick links)
│   ├── ApplyLeave.java         # Leave application (Full-day & Half-day 0.5 support)
│   ├── LeaveHistory.java       # Application history with employee self-cancellation
│   └── ManagerDashboard.java   # Manager portal (Approvals, Team Roster, Add Employee & Rollover)
│
├── database/
│   ├── create_tables.sql       # 3NF database schema, constraints, sequences
│   └── sample_data.sql         # Test dataset (2 managers, 4 employees, 3 leave types)
│
├── run.sh                      # Single-click startup script for Mac/Linux
├── run.bat                     # Single-click startup script for Windows
└── README.md                   # Full documentation & viva guide
```

---

## 🌟 Advanced Features Implemented

1. **Manager Team Management & Onboarding (`ManagerDashboard.java`)**:
   - Register new employees directly under the logged-in manager.
   - Automatically initializes default leave balances (12 Casual, 10 Sick, 15 Earned) via atomic JDBC Transactions.
2. **Leave Self-Cancellation by Employee (`LeaveHistory.java`)**:
   - Employees can cancel any `PENDING` leave request before manager review.
3. **Half-Day Leave Support (`ApplyLeave.java`)**:
   - Checkbox to apply for a Half-Day (`0.5` days) leave with automatic date locking and fractional quota deduction.
4. **Annual Year-End Leave Rollover (`ManagerDashboard.java`)**:
   - One-click team rollover: resets Casual and Sick quotas, and carries forward unused Earned leaves up to a 30-day cap.
5. **ACID Transaction Control**:
   - Atomic multi-table updates (`setAutoCommit(false)`, `commit()`, `rollback()`) on all approval and onboarding operations.

---

## 🚀 Quick Launch Instructions

### 1. Database Setup (Oracle)
Run both SQL scripts in your Oracle Database:
```bash
docker exec -i oracle-db sqlplus system/oracle@localhost:1521/FREEPDB1 < database/create_tables.sql
docker exec -i oracle-db sqlplus system/oracle@localhost:1521/FREEPDB1 < database/sample_data.sql
```

### 2. Launch the Desktop Application
* **macOS / Linux**:
  ```bash
  ./run.sh
  ```
* **Windows**:
  ```cmd
  run.bat
  ```

---

## 🔑 Demonstration Logins

| Role | Name | Email | Password | Manager |
| :--- | :--- | :--- | :--- | :--- |
| **Manager** | Alice Johnson | `alice@company.com` | `alice123` | - |
| **Manager** | Bob Williams | `bob@company.com` | `bob123` | - |
| **Employee** | John Doe | `john@company.com` | `john123` | Alice Johnson (1) |
| **Employee** | Jane Smith | `jane@company.com` | `jane123` | Alice Johnson (1) |
| **Employee** | Michael Brown | `michael@company.com` | `michael123` | Bob Williams (2) |
| **Employee** | Emily Davis | `emily@company.com` | `emily123` | Bob Williams (2) |
