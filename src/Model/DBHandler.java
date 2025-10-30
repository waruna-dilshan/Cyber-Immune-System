package Model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {

    private final String PRIMARY_URL = "jdbc:mysql://localhost:3306/user_db?allowPublicKeyRetrieval=true&useSSL=false";
    private final String BACKUP_URL = "jdbc:mysql://localhost:3306/user_backup_db?allowPublicKeyRetrieval=true&useSSL=false";
    private final String DB_USER = "root";
    private final String DB_PASS = "YOUR_DB_PASSWORD"; 
   
    public static boolean isPrimaryFailing = false;


    private Connection getConnection() throws SQLException {
        Connection con = null;

     
        try {
            if (isPrimaryFailing) {
               
                System.err.println("DBHandler: ATTACK IN PROGRESS! Throwing Simulated SQLException.");
                throw new SQLException("Simulated Connection Error: Primary DB Server is Down.");
            }
            
           
            con = DriverManager.getConnection(PRIMARY_URL, DB_USER, DB_PASS);
            return con;
            
        } catch (SQLException primaryError) {
           
            
            System.err.println("DBHandler: Primary DB Failed/Simulated Failure. Initiating Self-Healing (Switching to Backup).");

            try {
                
                con = DriverManager.getConnection(BACKUP_URL, DB_USER, DB_PASS);
                return con;

            } catch (SQLException backupError) {
               
                System.err.println("DBHandler: CRITICAL! Both Primary and Backup DBs are DOWN.");
                throw new SQLException("CRITICAL FAILURE: Cannot connect to any database.", backupError);
            }
        }
    }

   
    public String readUser(String username) {
        String sql = "SELECT email FROM users WHERE username = ?";

        try (Connection con = getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            String url = con.getMetaData().getURL();
            
           
            String dbSource = url.contains("user_backup_db") 
                              ? "Backup (Healed)"
                              : "Primary";

            if (rs.next()) {
                return "User Found! DB: " + dbSource + ", Email: " + rs.getString("email");
            } else {
                return "User Not Found."; 
            }

        } catch (SQLException e) {
            return "Fatal Error: Read operation failed! " + e.getMessage();
        }
    }
    
    
    private String executeInsert(String dbUrl, String username, String email) {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(dbUrl, DB_USER, DB_PASS);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            int rows = ps.executeUpdate();
            
            return (rows > 0) ? "SUCCESS" : "FAILED";
            
        } catch (SQLException e) {
            
            return "ERROR: " + e.getMessage();
        }
    }
    
   
    public String insertUser(String username, String email) {
        
        
        try {
            
            if (isPrimaryFailing) {
              
                System.err.println("DBHandler: ATTACK IN PROGRESS! Write attempt FAILED. Switching to Backup.");
                throw new SQLException("Simulated Write Error: Primary DB Server is Down.");
            }
            
            
            String primaryStatus = executeInsert(PRIMARY_URL, username, email);
            
            
            if (primaryStatus.contains("SUCCESS")) {
                executeInsert(BACKUP_URL, username, email);
                return "Signup Success! DB: Primary";
            } else {
               
                return "Signup Failed! Primary DB Write Error.";
            }
            
        } catch (SQLException primaryWriteError) {
           
            
            System.err.println("DBHandler: Primary Write Failed. Initiating Self-Healing (Writing to Backup).");
            
           
            String backupStatus = executeInsert(BACKUP_URL, username, email);
            
            if (backupStatus.contains("SUCCESS")) {
                return "Signup Success! DB: Backup (Healed)";
            } else {
                System.err.println("DBHandler: CRITICAL! Both DBs failed during Write.");
                return "Signup Failed! Both DBs offline.";
            }
        }
    }
}