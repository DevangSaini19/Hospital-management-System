package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database Connection Manager
 * Handles secure MySQL database connections using configuration properties
 * 
 * Configuration:
 * - Use environment variables: DB_URL, DB_USER, DB_PASSWORD
 * - Or use db.properties file in the resources folder
 * - Never hardcode credentials in source code
 */
public class DBConnection {
    private static final String CONFIG_FILE = "db.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    
    private static Connection connection = null;

    static {
        // Load configuration from environment variables or properties file
        loadConfiguration();
    }

    /**
     * Load database configuration from environment variables or properties file
     */
    private static void loadConfiguration() {
        try {
            // Try to load from environment variables first (more secure)
            URL = System.getenv("DB_URL");
            USER = System.getenv("DB_USER");
            PASSWORD = System.getenv("DB_PASSWORD");

            // Fall back to properties file if environment variables not set
            if (URL == null || USER == null || PASSWORD == null) {
                Properties props = new Properties();
                try (InputStream input = DBConnection.class.getClassLoader()
                        .getResourceAsStream(CONFIG_FILE)) {
                    if (input != null) {
                        props.load(input);
                        URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/hospital_db");
                        USER = props.getProperty("db.user", "root");
                        PASSWORD = props.getProperty("db.password", "");
                    } else {
                        // Use default values if no config file found
                        URL = "jdbc:mysql://localhost:3306/hospital_db";
                        USER = "root";
                        PASSWORD = "";  // Empty - user must provide via environment variables or db.properties
                        System.err.println("⚠️  WARNING: No db.properties file found. Please set DB_PASSWORD environment variable!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            // Set default values as fallback
            URL = "jdbc:mysql://localhost:3306/hospital_db";
            USER = "root";
            PASSWORD = "";
        }
    }

    /**
     * Get database connection
     * Creates a new connection if current one is closed or null
     * 
     * @return Database Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Check if connection is valid
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Database connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            String errorMsg = "MySQL JDBC Driver not found: " + e.getMessage();
            System.err.println(errorMsg);
            throw new SQLException(errorMsg, e);
        } catch (SQLException e) {
            String errorMsg = "Failed to connect to database: " + e.getMessage();
            System.err.println(errorMsg);
            throw new SQLException(errorMsg, e);
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Test the database connection
     */
    public static boolean testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null) {
                System.out.println("✓ Database connection test passed!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed: " + e.getMessage());
        }
        return false;
    }
}
