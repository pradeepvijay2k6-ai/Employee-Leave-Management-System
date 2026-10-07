-- ========================================================
-- Employee Leave Management System
-- File: database/sample_data.sql
-- Description: Sample test data for viva demonstration
-- ========================================================

-- Clean existing data
DELETE FROM LEAVE_REQUEST;
DELETE FROM LEAVE_BALANCE;
DELETE FROM EMPLOYEE;
DELETE FROM MANAGER;
DELETE FROM LEAVE_TYPE;

-- 1. INSERT MANAGERS (2 Managers)
INSERT INTO MANAGER (manager_id, name, email, password)
VALUES (1, 'Alice Johnson', 'alice@company.com', 'alice123');

INSERT INTO MANAGER (manager_id, name, email, password)
VALUES (2, 'Bob Williams', 'bob@company.com', 'bob123');

-- 2. INSERT EMPLOYEES (4 Employees across Departments)
-- Employees under Manager 1 (Alice Johnson)
INSERT INTO EMPLOYEE (employee_id, name, email, password, department, manager_id)
VALUES (101, 'John Doe', 'john@company.com', 'john123', 'Engineering', 1);

INSERT INTO EMPLOYEE (employee_id, name, email, password, department, manager_id)
VALUES (102, 'Jane Smith', 'jane@company.com', 'jane123', 'Human Resources', 1);

-- Employees under Manager 2 (Bob Williams)
INSERT INTO EMPLOYEE (employee_id, name, email, password, department, manager_id)
VALUES (103, 'Michael Brown', 'michael@company.com', 'michael123', 'Finance', 2);

INSERT INTO EMPLOYEE (employee_id, name, email, password, department, manager_id)
VALUES (104, 'Emily Davis', 'emily@company.com', 'emily123', 'Marketing', 2);

-- 3. INSERT LEAVE TYPES (3 Types)
INSERT INTO LEAVE_TYPE (leave_type_id, leave_type_name)
VALUES (1, 'Casual Leave');

INSERT INTO LEAVE_TYPE (leave_type_id, leave_type_name)
VALUES (2, 'Sick Leave');

INSERT INTO LEAVE_TYPE (leave_type_id, leave_type_name)
VALUES (3, 'Earned Leave');

-- 4. INSERT LEAVE BALANCES (Standard quota: Casual=12, Sick=10, Earned=15)
-- John Doe (101)
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (101, 1, 12, 2, 10);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (101, 2, 10, 0, 10);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (101, 3, 15, 0, 15);

-- Jane Smith (102)
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (102, 1, 12, 0, 12);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (102, 2, 10, 3, 7);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (102, 3, 15, 0, 15);

-- Michael Brown (103)
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (103, 1, 12, 0, 12);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (103, 2, 10, 0, 10);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (103, 3, 15, 0, 15);

-- Emily Davis (104)
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (104, 1, 12, 0, 12);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (104, 2, 10, 0, 10);
INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days)
VALUES (104, 3, 15, 0, 15);

-- 5. INSERT SAMPLE LEAVE REQUESTS (Pending, Approved, Rejected)
-- 1001: John Doe - Approved Casual Leave (2 days)
INSERT INTO LEAVE_REQUEST (request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date)
VALUES (1001, 101, 1, TO_DATE('2026-10-10', 'YYYY-MM-DD'), TO_DATE('2026-10-11', 'YYYY-MM-DD'), 2, 'Family function', 'APPROVED', TO_DATE('2026-10-01', 'YYYY-MM-DD'));

-- 1002: Jane Smith - Approved Sick Leave (3 days)
INSERT INTO LEAVE_REQUEST (request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date)
VALUES (1002, 102, 2, TO_DATE('2026-10-05', 'YYYY-MM-DD'), TO_DATE('2026-10-07', 'YYYY-MM-DD'), 3, 'Viral fever recovery', 'APPROVED', TO_DATE('2026-10-03', 'YYYY-MM-DD'));

-- 1003: Michael Brown - Rejected Earned Leave (5 days)
INSERT INTO LEAVE_REQUEST (request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date)
VALUES (1003, 103, 3, TO_DATE('2026-10-15', 'YYYY-MM-DD'), TO_DATE('2026-10-19', 'YYYY-MM-DD'), 5, 'Personal trip during audit week', 'REJECTED', TO_DATE('2026-10-02', 'YYYY-MM-DD'));

-- 1004: John Doe - Pending Sick Leave (1 day) -> Assigned to Manager 1 (Alice)
INSERT INTO LEAVE_REQUEST (request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date)
VALUES (1004, 101, 2, TO_DATE('2026-10-20', 'YYYY-MM-DD'), TO_DATE('2026-10-20', 'YYYY-MM-DD'), 1, 'Dental checkup', 'PENDING', TO_DATE('2026-10-06', 'YYYY-MM-DD'));

-- 1005: Emily Davis - Pending Casual Leave (2 days) -> Assigned to Manager 2 (Bob)
INSERT INTO LEAVE_REQUEST (request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date)
VALUES (1005, 104, 1, TO_DATE('2026-10-22', 'YYYY-MM-DD'), TO_DATE('2026-10-23', 'YYYY-MM-DD'), 2, 'Attending wedding', 'PENDING', TO_DATE('2026-10-07', 'YYYY-MM-DD'));

COMMIT;
