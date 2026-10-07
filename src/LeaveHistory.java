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
 * Displays past and current leave requests with stylized status badges.
 */
public class LeaveHistory extends JFrame {

    private int employeeId;
    private String employeeName;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton closeButton;

    public LeaveHistory(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;

        setTitle("Leave Application History - " + employeeName);
        setSize(920, 520);
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

        JLabel subLabel = new JLabel("Track the approval status of all your submitted leave applications.");
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

        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(new Color(30, 41, 59));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        // Set column widths
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

        bottomBar.add(refreshButton);
        bottomBar.add(closeButton);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        add(mainPanel);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHistory();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
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
                            rs.getInt("number_of_days"),
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
}
