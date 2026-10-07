import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * ApplyLeave.java
 * Form screen for submitting leave applications with automatic day calculation and quota validation.
 */
public class ApplyLeave extends JFrame {

    private int employeeId;
    private String employeeName;
    private EmployeeDashboard parentDashboard;

    private JComboBox<LeaveTypeItem> leaveTypeComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField daysField;
    private JTextArea reasonArea;
    private JButton calculateButton;
    private JButton submitButton;
    private JButton cancelButton;

    private static class LeaveTypeItem {
        int id;
        String name;

        LeaveTypeItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public ApplyLeave(int employeeId, String employeeName, EmployeeDashboard parentDashboard) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.parentDashboard = parentDashboard;

        setTitle("Apply for Leave - " + employeeName);
        setSize(540, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentDashboard);
        setResizable(false);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
        loadLeaveTypes();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Submit Leave Application");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subLabel = new JLabel("Fill in the dates and reason below for manager review.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Card Form
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Leave Type
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel typeLbl = new JLabel("Leave Type:");
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formCard.add(typeLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        leaveTypeComboBox = new JComboBox<>();
        leaveTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leaveTypeComboBox.setBackground(Color.WHITE);
        leaveTypeComboBox.setPreferredSize(new Dimension(200, 32));
        formCard.add(leaveTypeComboBox, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel startLbl = new JLabel("Start Date (YYYY-MM-DD):");
        startLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formCard.add(startLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        startDateField = new JTextField();
        startDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        startDateField.setPreferredSize(new Dimension(200, 32));
        startDateField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        formCard.add(startDateField, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel endLbl = new JLabel("End Date (YYYY-MM-DD):");
        endLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formCard.add(endLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        endDateField = new JTextField();
        endDateField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        endDateField.setPreferredSize(new Dimension(200, 32));
        endDateField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        formCard.add(endDateField, gbc);

        // Calculated Days
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel daysLbl = new JLabel("Calculated Days:");
        daysLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formCard.add(daysLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JPanel daysPanel = new JPanel(new BorderLayout(8, 0));
        daysPanel.setOpaque(false);

        daysField = new JTextField();
        daysField.setEditable(false);
        daysField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        daysField.setHorizontalAlignment(SwingConstants.CENTER);
        daysField.setBackground(new Color(241, 245, 249));
        daysField.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));

        calculateButton = new JButton("Calculate");
        calculateButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        calculateButton.setBackground(new Color(37, 99, 235));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setOpaque(true);
        calculateButton.setBorderPainted(false);
        calculateButton.setFocusPainted(false);
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        daysPanel.add(daysField, BorderLayout.CENTER);
        daysPanel.add(calculateButton, BorderLayout.EAST);
        formCard.add(daysPanel, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel reasonLbl = new JLabel("Reason for Leave:");
        reasonLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formCard.add(reasonLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        reasonArea = new JTextArea(3, 15);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(new LineBorder(new Color(203, 213, 225), 1, true));
        formCard.add(reasonScroll, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Bottom Actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(248, 250, 252));

        submitButton = new JButton("Submit Application");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(new Color(16, 185, 129)); // Emerald Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(175, 38));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(100, 116, 139));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(110, 38));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Events
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateDays();
            }
        });

        endDateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculateDays();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitApplication();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadLeaveTypes() {
        String sql = "SELECT leave_type_id, leave_type_name FROM LEAVE_TYPE ORDER BY leave_type_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            leaveTypeComboBox.removeAllItems();
            while (rs.next()) {
                leaveTypeComboBox.addItem(new LeaveTypeItem(
                        rs.getInt("leave_type_id"),
                        rs.getString("leave_type_name")
                ));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading leave types: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateDays() {
        String startStr = startDateField.getText().trim();
        String endStr = endDateField.getText().trim();

        if (startStr.isEmpty() || endStr.isEmpty()) {
            daysField.setText("");
            return -1;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            java.util.Date startDate = sdf.parse(startStr);
            java.util.Date endDate = sdf.parse(endStr);

            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be earlier than start date!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                daysField.setText("");
                return -1;
            }

            long diffInMillis = endDate.getTime() - startDate.getTime();
            int days = (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1;

            daysField.setText(String.valueOf(days));
            return days;
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter dates in valid YYYY-MM-DD format!", "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
            daysField.setText("");
            return -1;
        }
    }

    private void submitApplication() {
        LeaveTypeItem selectedType = (LeaveTypeItem) leaveTypeComboBox.getSelectedItem();
        if (selectedType == null) {
            JOptionPane.showMessageDialog(this, "Please select a leave category.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int days = calculateDays();
        if (days <= 0) return;

        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a reason for the leave application.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String startStr = startDateField.getText().trim();
        String endStr = endDateField.getText().trim();

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // 1. Check remaining balance
            String checkSql = "SELECT remaining_days FROM LEAVE_BALANCE WHERE employee_id = ? AND leave_type_id = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, employeeId);
            checkStmt.setInt(2, selectedType.id);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                int remainingDays = rs.getInt("remaining_days");

                if (days > remainingDays) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient leave balance for " + selectedType.name + "!\n" +
                            "Available Quota: " + remainingDays + " day(s)\n" +
                            "Requested: " + days + " day(s)",
                            "Balance Exceeded",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "No leave quota found for this type.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Insert with SEQ_REQUEST_ID.NEXTVAL
            String insertSql = "INSERT INTO LEAVE_REQUEST " +
                    "(request_id, employee_id, leave_type_id, start_date, end_date, number_of_days, reason, status, applied_date) " +
                    "VALUES (SEQ_REQUEST_ID.NEXTVAL, ?, ?, ?, ?, ?, ?, 'PENDING', SYSDATE)";

            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, employeeId);
            insertStmt.setInt(2, selectedType.id);
            insertStmt.setDate(3, Date.valueOf(startStr));
            insertStmt.setDate(4, Date.valueOf(endStr));
            insertStmt.setInt(5, days);
            insertStmt.setString(6, reason);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Leave application submitted successfully!\nStatus: PENDING manager review.",
                        "Application Submitted",
                        JOptionPane.INFORMATION_MESSAGE);

                if (parentDashboard != null) {
                    parentDashboard.loadLeaveBalances();
                }
                dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
