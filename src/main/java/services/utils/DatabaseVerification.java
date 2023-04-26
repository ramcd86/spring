package services.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseVerification {

    private final Properties info = new Properties();

    public void validateUserTable() throws SQLException {

        info.put("user", Environment.USER);
        info.put("password", Environment.PASS);

        String userTableDbQuery = "CREATE TABLE IF NOT EXISTS users (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    userName VARCHAR(100) NOT NULL,\n" +
                "    firstName VARCHAR(100) NOT NULL,\n" +
                "    lastName VARCHAR(100) NOT NULL,\n" +
                "    email VARCHAR(100) NOT NULL,\n" +
                "    hash VARCHAR(256) NOT NULL,\n" +
                "    salt VARCHAR(256) NOT NULL,\n" +
                "    authKey VARCHAR(256) NOT NULL,\n" +
                "    authKeyExpiry VARCHAR(50) NOT NULL,\n" +
                "    dob VARCHAR(50) NOT NULL,\n" +
                "    avatar BLOB,\n " +
                "    uuid VARCHAR(100) NOT NULL,\n" +
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

    public void validateStoreTable() throws SQLException {

        info.put("user", Environment.USER);
        info.put("password", Environment.PASS);

        String storeTableDbQuery = "CREATE TABLE IF NOT EXISTS stores (\n" +
                "    store_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    storeTitle VARCHAR(100) NOT NULL,\n" +
                "    storeDescription TEXT(1000) NOT NULL,\n" +
                "    canMessage BOOLEAN NOT NULL,\n" +
                "    isPrivate BOOLEAN NOT NULL,\n" +
                "    storeTheme VARCHAR(50) NOT NULL,\n" +
                "    storeItems TEXT(20000) NOT NULL,\n" +
                "    craftTags TEXT(1000) NOT NULL,\n" +
                "    parentUUID VARCHAR(100) NOT NULL,\n" +
                "    ownUUID VARCHAR(100) NOT NULL,\n" +
                "    endorsements VARCHAR(10000) NOT NULL,\n " +
                "    storeMessages TEXT(30000) NOT NULL,\n" +
                "    storeReviews TEXT(30000) NOT NULL,\n" +
                "    storeBanner BLOB\n " +
                ");";
        try {
            Connection conn = DriverManager.getConnection(Environment.DB_URL, info);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(storeTableDbQuery);
            System.out.println("storeTableDbQuery verified.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
