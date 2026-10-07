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
 * LeaveHistory.java
 * Displays employee leave requests and enables self-cancellation for pending applications.
 */
public class LeaveHistory extends JFrame {

    private int employeeId;
    private String employeeName;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton cancelReqButton;
    private JButton refreshButton;
    private JButton closeButton;

    public LeaveHistory(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;

        setTitle("Leave Application History - " + employeeName);
        setSize(960, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
        loadHistory();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("My Leave Application History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(15, 23, 42));

        JLabel subLabel = new JLabel("Track status or cancel any of your PENDING leave applications.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        String[] columns = {"Req #", "Category", "Start Date", "End Date", "Days", "Reason", "Status", "Applied On"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.setRowHeight(32);
        historyTable.setShowVerticalLines(false);
        historyTable.setGridColor(new Color(241, 245, 249));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setSelectionBackground(new Color(224, 231, 255));
        historyTable.setSelectionForeground(Color.BLACK);

        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(30, 41, 59));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        // Column widths
        historyTable.getColumnModel().getColumn(0).setMaxWidth(65);
        historyTable.getColumnModel().getColumn(4).setMaxWidth(65);

        // Custom status badge renderer
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (value != null) {
                    String status = value.toString();
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(5, 150, 105)); // Green
                        lbl.setText("● APPROVED");
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(220, 38, 38)); // Red
                        lbl.setText("● REJECTED");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(217, 119, 6)); // Amber
                        lbl.setText("⏳ PENDING");
                    } else if ("CANCELLED".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(100, 116, 139)); // Slate
                        lbl.setText("✕ CANCELLED");
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new LineBorder(new Color(226, 232, 240), 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tableCard, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomBar.setBackground(new Color(248, 250, 252));

        cancelReqButton = new JButton("✕ Cancel Selected Request");
        cancelReqButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelReqButton.setBackground(new Color(239, 68, 68));
        cancelReqButton.setForeground(Color.WHITE);
        cancelReqButton.setOpaque(true);
        cancelReqButton.setBorderPainted(false);
        cancelReqButton.setFocusPainted(false);
        cancelReqButton.setPreferredSize(new Dimension(200, 36));
        cancelReqButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBackground(new Color(14, 165, 233));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 36));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeButton.setBackground(new Color(100, 116, 139));
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(100, 36));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomBar.add(cancelReqButton);
        bottomBar.add(refreshButton);
        bottomBar.add(closeButton);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        // Events
        cancelReqButton.addActionListener(e -> cancelSelectedRequest());
        refreshButton.addActionListener(e -> loadHistory());
        closeButton.addActionListener(e -> dispose());
    }

    private void loadHistory() {
        tableModel.setRowCount(0);

        String sql = "SELECT lr.request_id, lt.leave_type_name, lr.start_date, lr.end_date, " +
                     "lr.number_of_days, lr.reason, lr.status, lr.applied_date " +
                     "FROM LEAVE_REQUEST lr " +
                     "JOIN LEAVE_TYPE lt ON lr.leave_type_id = lt.leave_type_id " +
                     "WHERE lr.employee_id = ? " +
                     "ORDER BY lr.applied_date DESC, lr.request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("request_id"),
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

    private void cancelSelectedRequest() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request from the table to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING requests can be cancelled. Status is currently: " + status, "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel Leave Request #" + requestId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE LEAVE_REQUEST SET status = 'CANCELLED' WHERE request_id = ? AND employee_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);
            pstmt.setInt(2, employeeId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Leave Request #" + requestId + " has been CANCELLED.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadHistory();
            } else {
                JOptionPane.showMessageDialog(this, "Request could not be cancelled or is no longer pending.", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
