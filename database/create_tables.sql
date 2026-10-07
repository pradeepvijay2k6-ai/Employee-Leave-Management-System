-- ========================================================
-- Employee Leave Management System
-- File: database/create_tables.sql
-- Description: Table creations, sequences, and constraints
-- ========================================================

-- Drop existing tables and sequences if they already exist (safe clean run)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEAVE_REQUEST CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEAVE_BALANCE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE LEAVE_TYPE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE EMPLOYEE CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE MANAGER CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_REQUEST_ID';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_EMPLOYEE_ID';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- 1. MANAGER TABLE
CREATE TABLE MANAGER (
    manager_id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    password VARCHAR2(50) NOT NULL
);

-- 2. EMPLOYEE TABLE
CREATE TABLE EMPLOYEE (
    employee_id NUMBER PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    password VARCHAR2(50) NOT NULL,
    department VARCHAR2(50) NOT NULL,
    manager_id NUMBER NOT NULL,
    CONSTRAINT fk_emp_manager FOREIGN KEY (manager_id) REFERENCES MANAGER(manager_id)
);

-- 3. LEAVE_TYPE TABLE
CREATE TABLE LEAVE_TYPE (
    leave_type_id NUMBER PRIMARY KEY,
    leave_type_name VARCHAR2(50) UNIQUE NOT NULL
);

-- 4. LEAVE_BALANCE TABLE
CREATE TABLE LEAVE_BALANCE (
    employee_id NUMBER NOT NULL,
    leave_type_id NUMBER NOT NULL,
    total_days NUMBER NOT NULL CHECK (total_days >= 0),
    used_days NUMBER DEFAULT 0 NOT NULL CHECK (used_days >= 0),
    remaining_days NUMBER NOT NULL CHECK (remaining_days >= 0),
    CONSTRAINT pk_leave_balance PRIMARY KEY (employee_id, leave_type_id),
    CONSTRAINT fk_lb_employee FOREIGN KEY (employee_id) REFERENCES EMPLOYEE(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_lb_leavetype FOREIGN KEY (leave_type_id) REFERENCES LEAVE_TYPE(leave_type_id)
);

-- 5. LEAVE_REQUEST TABLE
CREATE TABLE LEAVE_REQUEST (
    request_id NUMBER PRIMARY KEY,
    employee_id NUMBER NOT NULL,
    leave_type_id NUMBER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_days NUMBER NOT NULL CHECK (number_of_days > 0),
    reason VARCHAR2(255) NOT NULL,
    status VARCHAR2(20) DEFAULT 'PENDING' NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    applied_date DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_lr_employee FOREIGN KEY (employee_id) REFERENCES EMPLOYEE(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_lr_leavetype FOREIGN KEY (leave_type_id) REFERENCES LEAVE_TYPE(leave_type_id),
    CONSTRAINT chk_date_validity CHECK (end_date >= start_date)
);

-- Sequences for auto-generating Primary Keys
CREATE SEQUENCE SEQ_REQUEST_ID START WITH 2001 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_EMPLOYEE_ID START WITH 201 INCREMENT BY 1 NOCACHE;

COMMIT;
