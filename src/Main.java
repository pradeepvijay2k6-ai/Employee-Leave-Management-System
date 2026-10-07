import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main.java
 * Main application entry point for the Employee Leave Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Set native system look and feel for clean desktop appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Default swing look and feel will be used if system L&F fails
        }

        // Launch the Login screen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}
