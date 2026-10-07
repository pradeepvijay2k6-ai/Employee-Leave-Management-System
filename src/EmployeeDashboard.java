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
 * EmployeeDashboard.java
 * Modern Employee Dashboard with live quota summary, leave application, and self-cancellation tracking.
 */
public class EmployeeDashboard extends JFrame {

    private int employeeId;
    private String employeeName;
    private String department;

    private JTable balanceTable;
    private DefaultTableModel tableModel;
    private JButton applyLeaveButton;
    private JButton viewHistoryButton;
    private JButton refreshButton;
    private JButton logoutButton;

    public EmployeeDashboard(int employeeId, String employeeName, String department) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;

        setTitle("Employee Portal - " + employeeName);
        setSize(880, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
        loadLeaveBalances();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Header Banner Card
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(new Color(15, 23, 42)); // Slate 900
        headerCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel profileInfo = new JPanel(new GridLayout(2, 1, 3, 3));
        profileInfo.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + employeeName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subInfoLabel = new JLabel("Employee ID: #" + employeeId + "  |  Department: " + department);
        subInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subInfoLabel.setForeground(new Color(148, 163, 184));

        profileInfo.add(welcomeLabel);
        profileInfo.add(subInfoLabel);
        headerCard.add(profileInfo, BorderLayout.WEST);

        // Apply quick button in header
        applyLeaveButton = new JButton("+ Apply Leave");
        applyLeaveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        applyLeaveButton.setBackground(new Color(16, 185, 129));
        applyLeaveButton.setForeground(Color.WHITE);
        applyLeaveButton.setOpaque(true);
        applyLeaveButton.setBorderPainted(false);
        applyLeaveButton.setFocusPainted(false);
        applyLeaveButton.setPreferredSize(new Dimension(140, 38));
        applyLeaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerCard.add(applyLeaveButton, BorderLayout.EAST);

        mainPanel.add(headerCard, BorderLayout.NORTH);

        // 2. Center Card: Leave Balance Table
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel tableTitle = new JLabel("Your Allocated Leave Balances");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 41, 59));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Leave Category", "Total Quota (Days)", "Used (Days)", "Remaining Available (Days)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        balanceTable = new JTable(tableModel);
        balanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        balanceTable.setRowHeight(34);
        balanceTable.setShowVerticalLines(false);
        balanceTable.setGridColor(new Color(241, 245, 249));
        balanceTable.setSelectionBackground(new Color(224, 231, 255));
        balanceTable.setSelectionForeground(Color.BLACK);

        balanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        balanceTable.getTableHeader().setBackground(new Color(30, 41, 59));
        balanceTable.getTableHeader().setForeground(Color.WHITE);
        balanceTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        balanceTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        balanceTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        balanceTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(getFont().deriveFont(Font.BOLD, 14f));
                setForeground(new Color(5, 150, 105));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(balanceTable);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableCard, BorderLayout.CENTER);

        // 3. Bottom Controls
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomBar.setBackground(new Color(248, 250, 252));

        viewHistoryButton = new JButton("View My Leave History");
        viewHistoryButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewHistoryButton.setBackground(new Color(79, 70, 229));
        viewHistoryButton.setForeground(Color.WHITE);
        viewHistoryButton.setOpaque(true);
        viewHistoryButton.setBorderPainted(false);
        viewHistoryButton.setFocusPainted(false);
        viewHistoryButton.setPreferredSize(new Dimension(190, 36));
        viewHistoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBackground(new Color(14, 165, 233));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 36));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setBackground(new Color(220, 38, 38));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 36));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomBar.add(viewHistoryButton);
        bottomBar.add(refreshButton);
        bottomBar.add(logoutButton);

        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        // Events
        applyLeaveButton.addActionListener(e -> new ApplyLeave(employeeId, employeeName, EmployeeDashboard.this).setVisible(true));
        viewHistoryButton.addActionListener(e -> new LeaveHistory(employeeId, employeeName).setVisible(true));
        refreshButton.addActionListener(e -> loadLeaveBalances());
        logoutButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    public void loadLeaveBalances() {
        tableModel.setRowCount(0);

        String sql = "SELECT lt.leave_type_name, lb.total_days, lb.used_days, lb.remaining_days " +
                     "FROM LEAVE_BALANCE lb " +
                     "JOIN LEAVE_TYPE lt ON lb.leave_type_id = lt.leave_type_id " +
                     "WHERE lb.employee_id = ? " +
                     "ORDER BY lt.leave_type_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("leave_type_name");
                    double total = rs.getDouble("total_days");
                    double used = rs.getDouble("used_days");
                    double remaining = rs.getDouble("remaining_days");

                    String totalStr = (total == (int) total) ? String.valueOf((int) total) : String.valueOf(total);
                    String usedStr = (used == (int) used) ? String.valueOf((int) used) : String.valueOf(used);
                    String remStr = (remaining == (int) remaining) ? String.valueOf((int) remaining) : String.valueOf(remaining);

                    tableModel.addRow(new Object[]{type, totalStr, usedStr, remStr});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading leave balances: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
