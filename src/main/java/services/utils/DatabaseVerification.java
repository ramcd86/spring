package services.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseVerification {

    private final Properties info = new Properties();

    public DatabaseVerification() {

    }

    public void validateUserTable() throws SQLException {

        info.put("user", Environment.USER);
        info.put("password", Environment.PASS);

        String userTableDbQuery = "CREATE TABLE IF NOT EXISTS users (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    userName VARCHAR(50) NOT NULL,\n" +
                "    firstName VARCHAR(50) NOT NULL,\n" +
                "    lastName VARCHAR(50) NOT NULL,\n" +
                "    email VARCHAR(100) NOT NULL,\n" +
                "    hash VARCHAR(50) NOT NULL,\n" +
                "    salt VARCHAR(50) NOT NULL,\n" +
                "    authKey VARCHAR(50) NOT NULL,\n" +
                "    authKeyExpiry VARCHAR(50) NOT NULL,\n" +
                "    dob VARCHAR(50) NOT NULL,\n" +
                "    avatar BLOB,\n " +
                "    registrationDate VARCHAR(50) NOT NULL\n" +
                ");";
        try {
            Connection conn = DriverManager.getConnection(Environment.DB_URL, info);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(userTableDbQuery);
            System.out.println("userTableDbQuery verified.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
