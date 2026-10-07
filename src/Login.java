import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Login.java
 * Modernized authentication screen for Employees and Managers.
 */
public class Login extends JFrame {

    private JComboBox<String> roleComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton resetButton;

    public Login() {
        setTitle("Employee Leave Management System - Login");
        setSize(520, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(248, 250, 252));

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        // 1. Top Header Banner
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBackground(new Color(248, 250, 252));

        JLabel titleLabel = new JLabel("Leave Management Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42)); // Slate 900

        JLabel subtitleLabel = new JLabel("Sign in to manage leaves, quotas, and approvals", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 116, 139)); // Slate 500

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Card Form Panel
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Role Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel roleLabel = new JLabel("Login As:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setForeground(new Color(51, 65, 85));
        cardPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        roleComboBox = new JComboBox<>(new String[]{"Employee", "Manager"});
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(new Dimension(200, 34));
        cardPanel.add(roleComboBox, gbc);

        // Email Address
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(new Color(51, 65, 85));
        cardPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setPreferredSize(new Dimension(200, 34));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(51, 65, 85));
        cardPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(200, 34));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(passwordField, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // 3. Bottom Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(248, 250, 252));

        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(37, 99, 235)); // Royal Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(130, 38));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setBackground(new Color(100, 116, 139)); // Slate
        resetButton.setForeground(Color.WHITE);
        resetButton.setOpaque(true);
        resetButton.setBorderPainted(false);
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(110, 38));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event Handlers
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailField.setText("");
                passwordField.setText("");
                roleComboBox.setSelectedIndex(0);
                emailField.requestFocus();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String role = (String) roleComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Email and Password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            if ("Employee".equalsIgnoreCase(role)) {
                String sql = "SELECT employee_id, name, department FROM EMPLOYEE WHERE email = ? AND password = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    int empId = rs.getInt("employee_id");
                    String empName = rs.getString("name");
                    String dept = rs.getString("department");

                    new EmployeeDashboard(empId, empName, dept).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Employee credentials.\nPlease check your email and password.",
                            "Authentication Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if ("Manager".equalsIgnoreCase(role)) {
                String sql = "SELECT manager_id, name FROM MANAGER WHERE email = ? AND password = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    int mgrId = rs.getInt("manager_id");
                    String mgrName = rs.getString("name");

                    new ManagerDashboard(mgrId, mgrName).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid Manager credentials.\nPlease check your email and password.",
                            "Authentication Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Connection Error: " + ex.getMessage() + "\nEnsure Oracle Database container is running.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
