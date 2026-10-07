import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ManagerDashboard.java
 * Modern Manager Dashboard supporting:
 * 1. Pending leave review with ACID transactions & Half-Day support.
 * 2. Decision history tracking.
 * 3. Team management with new employee onboarding.
 * 4. Year-end leave rollover and carry-forward calculation.
 */
public class ManagerDashboard extends JFrame {

    private int managerId;
    private String managerName;

    private JTabbedPane tabbedPane;
    private JTable pendingTable;
    private DefaultTableModel pendingModel;
    private JTable processedTable;
    private DefaultTableModel processedModel;
    private JTable employeeTable;
    private DefaultTableModel employeeModel;

    private JButton approveButton;
    private JButton rejectButton;
    private JButton addEmployeeButton;
    private JButton rolloverButton;
    private JButton refreshButton;
    private JButton logoutButton;

    public ManagerDashboard(int managerId, String managerName) {
        this.managerId = managerId;
        this.managerName = managerName;

        setTitle("Manager Portal - " + managerName);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
        loadAllData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Header Banner Card
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(new Color(15, 23, 42)); // Slate 900
        headerCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel profileInfo = new JPanel(new GridLayout(2, 1, 3, 3));
        profileInfo.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Manager Portal: " + managerName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subInfoLabel = new JLabel("Manager ID: #" + managerId + "  |  Team Management, Leave Approvals & Rollovers");
        subInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subInfoLabel.setForeground(new Color(148, 163, 184));

        profileInfo.add(welcomeLabel);
        profileInfo.add(subInfoLabel);
        headerCard.add(profileInfo, BorderLayout.WEST);

        // Header Action: Add Employee & Rollover buttons
        JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerBtns.setOpaque(false);

        rolloverButton = new JButton("⚡ Year-End Rollover");
        rolloverButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rolloverButton.setBackground(new Color(99, 102, 241)); // Indigo
        rolloverButton.setForeground(Color.WHITE);
        rolloverButton.setOpaque(true);
        rolloverButton.setBorderPainted(false);
        rolloverButton.setFocusPainted(false);
        rolloverButton.setPreferredSize(new Dimension(170, 38));
        rolloverButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addEmployeeButton = new JButton("+ Add Employee");
        addEmployeeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addEmployeeButton.setBackground(new Color(16, 185, 129)); // Emerald Green
        addEmployeeButton.setForeground(Color.WHITE);
        addEmployeeButton.setOpaque(true);
        addEmployeeButton.setBorderPainted(false);
        addEmployeeButton.setFocusPainted(false);
        addEmployeeButton.setPreferredSize(new Dimension(150, 38));
        addEmployeeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerBtns.add(rolloverButton);
        headerBtns.add(addEmployeeButton);
        headerCard.add(headerBtns, BorderLayout.EAST);

        mainPanel.add(headerCard, BorderLayout.NORTH);

        // 2. Center Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        // ---------------- TAB 1: Pending Requests ----------------
        JPanel pendingPanel = new JPanel(new BorderLayout(0, 12));
        pendingPanel.setBackground(Color.WHITE);
        pendingPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] pendingCols = {"Req #", "Emp #", "Employee Name", "Department", "Leave Category", "Type ID", "Start Date", "End Date", "Days", "Reason", "Applied Date"};
        pendingModel = new DefaultTableModel(pendingCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pendingTable = new JTable(pendingModel);
        pendingTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pendingTable.setRowHeight(32);
        pendingTable.setShowVerticalLines(false);
        pendingTable.setGridColor(new Color(241, 245, 249));
        pendingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingTable.setSelectionBackground(new Color(224, 231, 255));
        pendingTable.setSelectionForeground(Color.BLACK);

        pendingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pendingTable.getTableHeader().setBackground(new Color(30, 41, 59));
        pendingTable.getTableHeader().setForeground(Color.WHITE);
        pendingTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        // Hide internal Type ID
        pendingTable.getColumnModel().getColumn(5).setMinWidth(0);
        pendingTable.getColumnModel().getColumn(5).setMaxWidth(0);
        pendingTable.getColumnModel().getColumn(5).setWidth(0);

        pendingTable.getColumnModel().getColumn(0).setMaxWidth(60);
        pendingTable.getColumnModel().getColumn(1).setMaxWidth(60);
        pendingTable.getColumnModel().getColumn(8).setMaxWidth(60);

        JScrollPane pendingScroll = new JScrollPane(pendingTable);
        pendingScroll.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        pendingPanel.add(pendingScroll, BorderLayout.CENTER);

        // Actions for Pending Tab
        JPanel pendingActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pendingActions.setBackground(Color.WHITE);

        approveButton = new JButton("✓ Approve Leave");
        approveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        approveButton.setBackground(new Color(16, 185, 129));
        approveButton.setForeground(Color.WHITE);
        approveButton.setOpaque(true);
        approveButton.setBorderPainted(false);
        approveButton.setFocusPainted(false);
        approveButton.setPreferredSize(new Dimension(150, 38));
        approveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rejectButton = new JButton("✕ Reject Leave");
        rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rejectButton.setBackground(new Color(220, 38, 38));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);
        rejectButton.setFocusPainted(false);
        rejectButton.setPreferredSize(new Dimension(140, 38));
        rejectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pendingActions.add(approveButton);
        pendingActions.add(rejectButton);
        pendingPanel.add(pendingActions, BorderLayout.SOUTH);

        tabbedPane.addTab("  ⏳ Pending Requests  ", pendingPanel);

        // ---------------- TAB 2: Processed History ----------------
        JPanel processedPanel = new JPanel(new BorderLayout(0, 12));
        processedPanel.setBackground(Color.WHITE);
        processedPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] processedCols = {"Req #", "Emp #", "Employee Name", "Department", "Leave Category", "Start Date", "End Date", "Days", "Reason", "Decision Status", "Applied Date"};
        processedModel = new DefaultTableModel(processedCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        processedTable = new JTable(processedModel);
        processedTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        processedTable.setRowHeight(32);
        processedTable.setShowVerticalLines(false);
        processedTable.setGridColor(new Color(241, 245, 249));

        processedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        processedTable.getTableHeader().setBackground(new Color(30, 41, 59));
        processedTable.getTableHeader().setForeground(Color.WHITE);
        processedTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        processedTable.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (value != null) {
                    String st = value.toString();
                    if ("APPROVED".equalsIgnoreCase(st)) {
                        lbl.setForeground(new Color(5, 150, 105));
                        lbl.setText("● APPROVED");
                    } else if ("REJECTED".equalsIgnoreCase(st)) {
                        lbl.setForeground(new Color(220, 38, 38));
                        lbl.setText("● REJECTED");
                    } else if ("CANCELLED".equalsIgnoreCase(st)) {
                        lbl.setForeground(new Color(100, 116, 139));
                        lbl.setText("✕ CANCELLED");
                    }
                }
                return lbl;
            }
        });

        JScrollPane processedScroll = new JScrollPane(processedTable);
        processedScroll.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        processedPanel.add(processedScroll, BorderLayout.CENTER);

        tabbedPane.addTab("  📋 Decision History  ", processedPanel);

        // ---------------- TAB 3: Team Members ----------------
        JPanel teamPanel = new JPanel(new BorderLayout(0, 12));
        teamPanel.setBackground(Color.WHITE);
        teamPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] empCols = {"Emp ID", "Full Name", "Email Address", "Department", "Casual Rem.", "Sick Rem.", "Earned Rem."};
        employeeModel = new DefaultTableModel(empCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(employeeModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(32);
        employeeTable.setShowVerticalLines(false);
        employeeTable.setGridColor(new Color(241, 245, 249));

        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(new Color(30, 41, 59));
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        employeeTable.getColumnModel().getColumn(0).setMaxWidth(80);
        employeeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane empScroll = new JScrollPane(employeeTable);
        empScroll.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        teamPanel.add(empScroll, BorderLayout.CENTER);

        tabbedPane.addTab("  👥 My Team Employees  ", teamPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 3. Bottom Global Controls
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomBar.setBackground(new Color(248, 250, 252));

        refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBackground(new Color(14, 165, 233));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(130, 36));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setBackground(new Color(100, 116, 139));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 36));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomBar.add(refreshButton);
        bottomBar.add(logoutButton);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        // Event Handlers
        approveButton.addActionListener(e -> processLeaveRequest(true));
        rejectButton.addActionListener(e -> processLeaveRequest(false));
        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        rolloverButton.addActionListener(e -> performYearEndRollover());
        refreshButton.addActionListener(e -> loadAllData());
        logoutButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadAllData() {
        loadPendingRequests();
        loadProcessedRequests();
        loadTeamEmployees();
    }

    private void loadPendingRequests() {
        pendingModel.setRowCount(0);

        String sql = "SELECT lr.request_id, e.employee_id, e.name AS emp_name, e.department, " +
                     "lt.leave_type_name, lr.leave_type_id, lr.start_date, lr.end_date, " +
                     "lr.number_of_days, lr.reason, lr.applied_date " +
                     "FROM LEAVE_REQUEST lr " +
                     "JOIN EMPLOYEE e ON lr.employee_id = e.employee_id " +
                     "JOIN LEAVE_TYPE lt ON lr.leave_type_id = lt.leave_type_id " +
                     "WHERE e.manager_id = ? AND lr.status = 'PENDING' " +
                     "ORDER BY lr.applied_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, managerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pendingModel.addRow(new Object[]{
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("emp_name"),
                            rs.getString("department"),
                            rs.getString("leave_type_name"),
                            rs.getInt("leave_type_id"),
                            rs.getDate("start_date").toString(),
                            rs.getDate("end_date").toString(),
                            rs.getDouble("number_of_days"),
                            rs.getString("reason"),
                            rs.getDate("applied_date").toString()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading pending requests: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadProcessedRequests() {
        processedModel.setRowCount(0);

        String sql = "SELECT lr.request_id, e.employee_id, e.name AS emp_name, e.department, " +
                     "lt.leave_type_name, lr.start_date, lr.end_date, " +
                     "lr.number_of_days, lr.reason, lr.status, lr.applied_date " +
                     "FROM LEAVE_REQUEST lr " +
                     "JOIN EMPLOYEE e ON lr.employee_id = e.employee_id " +
                     "JOIN LEAVE_TYPE lt ON lr.leave_type_id = lt.leave_type_id " +
                     "WHERE e.manager_id = ? AND lr.status IN ('APPROVED', 'REJECTED', 'CANCELLED') " +
                     "ORDER BY lr.applied_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, managerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    processedModel.addRow(new Object[]{
                            rs.getInt("request_id"),
                            rs.getInt("employee_id"),
                            rs.getString("emp_name"),
                            rs.getString("department"),
                            rs.getString("leave_type_name"),
                            rs.getDate("start_date").toString(),
                            rs.getDate("end_date").toString(),
                            rs.getDouble("number_of_days"),
                            rs.getString("reason"),
                            rs.getString("status"),
                            rs.getDate("applied_date").toString()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadTeamEmployees() {
        employeeModel.setRowCount(0);

        String sql = "SELECT e.employee_id, e.name, e.email, e.department, " +
                     "NVL((SELECT remaining_days FROM LEAVE_BALANCE WHERE employee_id = e.employee_id AND leave_type_id = 1), 0) AS casual_rem, " +
                     "NVL((SELECT remaining_days FROM LEAVE_BALANCE WHERE employee_id = e.employee_id AND leave_type_id = 2), 0) AS sick_rem, " +
                     "NVL((SELECT remaining_days FROM LEAVE_BALANCE WHERE employee_id = e.employee_id AND leave_type_id = 3), 0) AS earned_rem " +
                     "FROM EMPLOYEE e " +
                     "WHERE e.manager_id = ? " +
                     "ORDER BY e.employee_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, managerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employeeModel.addRow(new Object[]{
                            rs.getInt("employee_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getDouble("casual_rem") + " days",
                            rs.getDouble("sick_rem") + " days",
                            rs.getDouble("earned_rem") + " days"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading team employees: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void processLeaveRequest(boolean isApprove) {
        int selectedRow = pendingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a leave request from the table first.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) pendingModel.getValueAt(selectedRow, 0);
        int empId = (int) pendingModel.getValueAt(selectedRow, 1);
        String empName = (String) pendingModel.getValueAt(selectedRow, 2);
        String leaveTypeName = (String) pendingModel.getValueAt(selectedRow, 4);
        int leaveTypeId = (int) pendingModel.getValueAt(selectedRow, 5);
        double days = (double) pendingModel.getValueAt(selectedRow, 8);

        String actionWord = isApprove ? "APPROVE" : "REJECT";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + actionWord + " leave request #" + requestId + " for " + empName + " (" + days + " days)?",
                "Confirm Decision",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement updateReqStmt = null;
        PreparedStatement checkBalanceStmt = null;
        PreparedStatement updateBalanceStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            if (isApprove) {
                // 1. Verify balance
                String checkSql = "SELECT remaining_days FROM LEAVE_BALANCE WHERE employee_id = ? AND leave_type_id = ?";
                checkBalanceStmt = conn.prepareStatement(checkSql);
                checkBalanceStmt.setInt(1, empId);
                checkBalanceStmt.setInt(2, leaveTypeId);
                rs = checkBalanceStmt.executeQuery();

                if (rs.next()) {
                    double remaining = rs.getDouble("remaining_days");
                    if (days > remaining) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this,
                                "Cannot Approve: Employee only has " + remaining + " remaining days of " + leaveTypeName + " (Requested: " + days + ").",
                                "Balance Underflow",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Error: Leave balance record not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Update Request Status
                String updateReqSql = "UPDATE LEAVE_REQUEST SET status = 'APPROVED' WHERE request_id = ? AND status = 'PENDING'";
                updateReqStmt = conn.prepareStatement(updateReqSql);
                updateReqStmt.setInt(1, requestId);
                int updatedReqs = updateReqStmt.executeUpdate();

                if (updatedReqs == 0) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Request was already processed or no longer pending.", "Conflict", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 3. Update Balance
                String updateBalSql = "UPDATE LEAVE_BALANCE SET used_days = used_days + ?, remaining_days = remaining_days - ? " +
                                      "WHERE employee_id = ? AND leave_type_id = ?";
                updateBalanceStmt = conn.prepareStatement(updateBalSql);
                updateBalanceStmt.setDouble(1, days);
                updateBalanceStmt.setDouble(2, days);
                updateBalanceStmt.setInt(3, empId);
                updateBalanceStmt.setInt(4, leaveTypeId);
                updateBalanceStmt.executeUpdate();

                conn.commit(); // Commit Transaction

                JOptionPane.showMessageDialog(this,
                        "Leave request #" + requestId + " APPROVED successfully!\n" +
                        days + " day(s) deducted from " + empName + "'s " + leaveTypeName + " balance.",
                        "Approval Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                // Reject
                String updateReqSql = "UPDATE LEAVE_REQUEST SET status = 'REJECTED' WHERE request_id = ? AND status = 'PENDING'";
                updateReqStmt = conn.prepareStatement(updateReqSql);
                updateReqStmt.setInt(1, requestId);
                int updatedReqs = updateReqStmt.executeUpdate();

                if (updatedReqs == 0) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Request was already processed or no longer pending.", "Conflict", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                conn.commit();

                JOptionPane.showMessageDialog(this,
                        "Leave request #" + requestId + " REJECTED.\nNo leave days were deducted.",
                        "Request Rejected",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            loadAllData();

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            JOptionPane.showMessageDialog(this,
                    "Transaction Failed: " + ex.getMessage() + "\nAll changes have been rolled back.",
                    "Transaction Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkBalanceStmt != null) checkBalanceStmt.close();
                if (updateReqStmt != null) updateReqStmt.close();
                if (updateBalanceStmt != null) updateBalanceStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(new Color(248, 250, 252));
        content.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Register New Team Member");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(15, 23, 42));
        content.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> deptBox = new JComboBox<>(new String[]{"Engineering", "Human Resources", "Finance", "Marketing", "Operations", "Quality Assurance"});
        deptBox.setBackground(Color.WHITE);

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel("Department:"));
        form.add(deptBox);

        content.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBackground(new Color(248, 250, 252));

        JButton saveBtn = new JButton("Add Employee");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setBackground(new Color(16, 185, 129));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(140, 36));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setBackground(new Color(100, 116, 139));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(90, 36));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String dept = (String) deptBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all employee fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection conn = null;
            PreparedStatement checkEmailStmt = null;
            PreparedStatement insertEmpStmt = null;
            PreparedStatement getEmpIdStmt = null;
            PreparedStatement insertBalStmt = null;
            ResultSet rs = null;

            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false); // Begin Transaction

                checkEmailStmt = conn.prepareStatement("SELECT email FROM EMPLOYEE WHERE email = ? UNION SELECT email FROM MANAGER WHERE email = ?");
                checkEmailStmt.setString(1, email);
                checkEmailStmt.setString(2, email);
                rs = checkEmailStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(dialog, "An account with this email address already exists.", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String insertEmpSql = "INSERT INTO EMPLOYEE (employee_id, name, email, password, department, manager_id) " +
                                      "VALUES (SEQ_EMPLOYEE_ID.NEXTVAL, ?, ?, ?, ?, ?)";
                insertEmpStmt = conn.prepareStatement(insertEmpSql);
                insertEmpStmt.setString(1, name);
                insertEmpStmt.setString(2, email);
                insertEmpStmt.setString(3, password);
                insertEmpStmt.setString(4, dept);
                insertEmpStmt.setInt(5, managerId);
                insertEmpStmt.executeUpdate();

                getEmpIdStmt = conn.prepareStatement("SELECT SEQ_EMPLOYEE_ID.CURRVAL AS emp_id FROM dual");
                rs = getEmpIdStmt.executeQuery();
                int newEmpId = 0;
                if (rs.next()) {
                    newEmpId = rs.getInt("emp_id");
                }

                // Default balances
                String insertBalSql = "INSERT INTO LEAVE_BALANCE (employee_id, leave_type_id, total_days, used_days, remaining_days) VALUES (?, ?, ?, 0, ?)";
                insertBalStmt = conn.prepareStatement(insertBalSql);

                // Casual
                insertBalStmt.setInt(1, newEmpId);
                insertBalStmt.setInt(2, 1);
                insertBalStmt.setDouble(3, 12.0);
                insertBalStmt.setDouble(4, 12.0);
                insertBalStmt.executeUpdate();

                // Sick
                insertBalStmt.setInt(1, newEmpId);
                insertBalStmt.setInt(2, 2);
                insertBalStmt.setDouble(3, 10.0);
                insertBalStmt.setDouble(4, 10.0);
                insertBalStmt.executeUpdate();

                // Earned
                insertBalStmt.setInt(1, newEmpId);
                insertBalStmt.setInt(2, 3);
                insertBalStmt.setDouble(3, 15.0);
                insertBalStmt.setDouble(4, 15.0);
                insertBalStmt.executeUpdate();

                conn.commit();

                JOptionPane.showMessageDialog(dialog,
                        "Employee Registered Successfully!\n\n" +
                        "Employee ID: " + newEmpId + "\n" +
                        "Name: " + name + "\n" +
                        "Email: " + email + "\n" +
                        "Department: " + dept + "\n" +
                        "Reporting Manager: " + managerName + "\n" +
                        "Default Leave Quota Initialized.",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadTeamEmployees();
                tabbedPane.setSelectedIndex(2);

            } catch (SQLException ex) {
                if (conn != null) {
                    try { conn.rollback(); } catch (SQLException ignored) {}
                }
                JOptionPane.showMessageDialog(dialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (checkEmailStmt != null) checkEmailStmt.close();
                    if (insertEmpStmt != null) insertEmpStmt.close();
                    if (getEmpIdStmt != null) getEmpIdStmt.close();
                    if (insertBalStmt != null) insertBalStmt.close();
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException ignored) {}
            }
        });

        dialog.add(content);
        dialog.setVisible(true);
    }

    /**
     * Executes annual leave rollover for all employees under this manager using a JDBC transaction.
     */
    private void performYearEndRollover() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Execute Annual Year-End Leave Rollover for your team?\n\n" +
                "• Casual Leave: Resets to 12 days quota.\n" +
                "• Sick Leave: Resets to 10 days quota.\n" +
                "• Earned Leave: Remaining balance carries forward (+15 new days, max 30 days cap).\n\n" +
                "This action applies to all reporting team members.",
                "Confirm Year-End Rollover",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        PreparedStatement stmtCasual = null;
        PreparedStatement stmtSick = null;
        PreparedStatement stmtEarned = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Reset Casual Leaves
            String sqlCasual = "UPDATE LEAVE_BALANCE SET total_days = 12, used_days = 0, remaining_days = 12 " +
                               "WHERE employee_id IN (SELECT employee_id FROM EMPLOYEE WHERE manager_id = ?) AND leave_type_id = 1";
            stmtCasual = conn.prepareStatement(sqlCasual);
            stmtCasual.setInt(1, managerId);
            stmtCasual.executeUpdate();

            // 2. Reset Sick Leaves
            String sqlSick = "UPDATE LEAVE_BALANCE SET total_days = 10, used_days = 0, remaining_days = 10 " +
                             "WHERE employee_id IN (SELECT employee_id FROM EMPLOYEE WHERE manager_id = ?) AND leave_type_id = 2";
            stmtSick = conn.prepareStatement(sqlSick);
            stmtSick.setInt(1, managerId);
            stmtSick.executeUpdate();

            // 3. Carry forward Earned Leaves with max cap 30
            String sqlEarned = "UPDATE LEAVE_BALANCE SET total_days = LEAST(remaining_days + 15, 30), used_days = 0, remaining_days = LEAST(remaining_days + 15, 30) " +
                               "WHERE employee_id IN (SELECT employee_id FROM EMPLOYEE WHERE manager_id = ?) AND leave_type_id = 3";
            stmtEarned = conn.prepareStatement(sqlEarned);
            stmtEarned.setInt(1, managerId);
            stmtEarned.executeUpdate();

            conn.commit(); // Commit Transaction

            JOptionPane.showMessageDialog(this,
                    "Annual Leave Rollover Completed Successfully!\nAll team leave quotas have been updated.",
                    "Rollover Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            loadAllData();
            tabbedPane.setSelectedIndex(2);

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            JOptionPane.showMessageDialog(this, "Rollover Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (stmtCasual != null) stmtCasual.close();
                if (stmtSick != null) stmtSick.close();
                if (stmtEarned != null) stmtEarned.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }
}
