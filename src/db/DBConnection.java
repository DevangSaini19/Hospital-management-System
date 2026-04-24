package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

/**
 * Database Connection Manager
 * Handles secure MySQL database connections using configuration properties
 * 
 * Configuration Priority:
 * 1. Environment variables: DB_URL, DB_USER, DB_PASSWORD
 * 2. db.properties file in src/config/
 * 3. db.properties file in bin/
 * 4. db.properties file in classpath
 * 5. Default values
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
     * Load database configuration from multiple sources
     */
    private static void loadConfiguration() {
        try {
            // Try to load from environment variables first (most secure)
            URL = System.getenv("DB_URL");
            USER = System.getenv("DB_USER");
            PASSWORD = System.getenv("DB_PASSWORD");

            // Fall back to properties file if environment variables not set
            if (URL == null || USER == null || PASSWORD == null) {
                Properties props = new Properties();
                boolean loaded = false;

                // Try multiple locations for db.properties
                String[] configPaths = {
                    "src/config/db.properties",                    // Development directory
                    "bin/db.properties",                           // Compiled directory
                    System.getProperty("user.dir") + "/src/config/db.properties",  // Absolute path
                    System.getProperty("user.dir") + "/bin/db.properties"           // Absolute path
                };

                // Try each path
                for (String path : configPaths) {
                    File configFile = new File(path);
                    if (configFile.exists()) {
                        try (FileInputStream fis = new FileInputStream(configFile)) {
                            props.load(fis);
                            loaded = true;
                            System.out.println("✓ Configuration loaded from: " + path);
                            break;
                        } catch (Exception e) {
                            System.err.println("Could not load from " + path + ": " + e.getMessage());
                        }
                    }
                }

                // Try classpath resource if file-based loading failed
                if (!loaded) {
                    try (InputStream input = DBConnection.class.getClassLoader()
                            .getResourceAsStream(CONFIG_FILE)) {
                        if (input != null) {
                            props.load(input);
                            loaded = true;
                            System.out.println("✓ Configuration loaded from classpath resource");
                        }
                    } catch (Exception e) {
                        System.err.println("Could not load from classpath: " + e.getMessage());
                    }
                }

                // Set properties
                URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/hospital_db");
                USER = props.getProperty("db.user", "root");
                PASSWORD = props.getProperty("db.password", "");

                if (!loaded || PASSWORD.isEmpty()) {
                    System.err.println("⚠️  WARNING: Database password not found!");
                    System.err.println("   Please ensure db.properties is accessible or set DB_PASSWORD environment variable");
                }
            }

            System.out.println("Database Configuration: URL=" + URL + ", USER=" + USER);
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
