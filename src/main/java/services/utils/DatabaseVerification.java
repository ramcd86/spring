package services.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseVerification {

    public static Connection getConnection() throws SQLException {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", Environment.USER);
        connectionProperties.put("password", Environment.PASS);
        return DriverManager.getConnection(Environment.DB_URL, connectionProperties);
    }

    public static DatabaseVerification databaseVerification() throws SQLException {
        return new DatabaseVerification();
    }

    public DatabaseVerification injectDbValidation(String query, String verificationMessage) {


        try {
            Connection conn = DatabaseVerification.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            LoggingUtils.log(verificationMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public DatabaseVerification validateUserTable() throws SQLException {


        String query = "CREATE TABLE IF NOT EXISTS users (\n" +
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

        return injectDbValidation(query, "User table validated.");

    }

    public DatabaseVerification validateStoreTable() throws SQLException {


        String query = "CREATE TABLE IF NOT EXISTS stores (\n" +
                "    store_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    storeTitle VARCHAR(100) NOT NULL,\n" +
                "    storeDescription TEXT(1000) NOT NULL,\n" +
                "    canMessage BOOLEAN NOT NULL,\n" +
                "    isPrivate BOOLEAN NOT NULL,\n" +
                "    storeTheme VARCHAR(50) NOT NULL,\n" +
                "    craftTags TEXT(1000) NOT NULL,\n" +
                "    addressLine1 TEXT(100) NOT NULL,\n" +
                "    addressLine2 TEXT(100) NOT NULL,\n" +
                "    addressLine3 TEXT(100) NOT NULL,\n" +
                "    postcode TEXT(100) NOT NULL,\n" +
                "    parentUUID VARCHAR(100) NOT NULL,\n" +
                "    ownUUID VARCHAR(100) NOT NULL,\n" +
                "    storeAvatar BLOB,\n " +
                "    storeBanner BLOB\n " +
                ");";

        return injectDbValidation(query, "Store table validated.");

    }

    public DatabaseVerification validateStoreReviews() throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS storeReviews (\n" +
                "    store_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    storeRating INT NOT NULL,\n" +
                "    parentUUID VARCHAR(200) NOT NULL,\n" +
                "    fromUser VARCHAR(200) NOT NULL,\n" +
                "    review TEXT(1000) NOT NULL\n" +
                ");";

        return injectDbValidation(query, "Store Reviews table validated.");

    }

    public DatabaseVerification validateStoreItems() throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS storeItems (\n" +
                "    store_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    storeItemName VARCHAR(200) NOT NULL,\n" +
                "    parentUUID VARCHAR(200) NOT NULL,\n" +
                "    storeItemImage BLOB,\n" +
                "    storeItemDescription TEXT(1000) NOT NULL,\n" +
                "    storeItemPrice VARCHAR(10) NOT NULL\n" +
                ");";

        return injectDbValidation(query, "Store Items table validated.");

    }

}
