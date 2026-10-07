import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Manages JDBC connection to the Oracle Database.
 */
public class DBConnection {

    // Database Connection Parameters for Oracle Free / 23ai / 21c / 19c
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
    private static final String USERNAME = "system";
    private static final String PASSWORD = "oracle";

    static {
        try {
            // Load Oracle JDBC Driver
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found! Ensure ojdbc8.jar is added to the classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a database Connection object.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
