package services.registration;

import com.hellokoding.springboot.view.userclasses.PublicUserDetails;
import com.hellokoding.springboot.view.userclasses.User;
import com.hellokoding.springboot.view.userclasses.UserAuthKey;
import com.hellokoding.springboot.view.userclasses.UserLoginDetails;
import org.springframework.stereotype.Service;
import services.utils.Environment;
import services.utils.HashUtils;
import services.utils.TimeUtils;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class UserManagementService {

    private final Properties dbProperties = new Properties();

    public boolean isAuthKeyValid(UserAuthKey authKey) throws Exception {
        dbProperties.put("user", Environment.USER);
        dbProperties.put("password", Environment.PASS);
        try (Connection conn = DriverManager.getConnection(Environment.DB_URL, dbProperties)) {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM users WHERE authKey = '" + authKey.getAuthKey() + "'");
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("No such auth key");
                }
                String authKeyTimeStamp = rs.getString("authKeyExpiry");
                return !TimeUtils.isISOStringOutOfDate(authKeyTimeStamp);
            }
        }
    }

    private String updateAuthKey(String email) throws Exception {
        dbProperties.put("user", Environment.USER);
        dbProperties.put("password", Environment.PASS);
        String salt = HashUtils.generateSalt();
        String authKey = HashUtils.hashWithSalt(email, salt);
        try (Connection conn = DriverManager.getConnection(Environment.DB_URL, dbProperties)) {
            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE users SET authKey = '" + authKey + "', authKeyExpiry = '"
                            + TimeUtils.getDateNowPlus6MonthsAsISODateTimeString() + "' WHERE email = '" + email
                            + "'");
            int rs = statement.executeUpdate();
            if (rs > 0) {
                return authKey;
            } else {
                throw new Exception("User not updated, or no such user.");
            }
        }
    }

    private String validateAndUpdateAuthKey(String authKey, String email) {
        UserAuthKey authKeyObject = new UserAuthKey();
        authKeyObject.setAuthKey(authKey);
        CompletableFuture<Boolean> isAuthKeyValid = CompletableFuture.supplyAsync(() -> {
            try {
                return isAuthKeyValid(authKeyObject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        boolean validatedAuthKey = isAuthKeyValid.join();
        if (validatedAuthKey) {
            return authKey;
        } else {
            CompletableFuture<String> newAuthKeyJoin = CompletableFuture.supplyAsync(() -> {
                try {
                    return updateAuthKey(email);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return newAuthKeyJoin.join();
        }
    }

    public PublicUserDetails getUser(UserLoginDetails userLoginDetails) throws Exception {
        dbProperties.put("user", Environment.USER);
        dbProperties.put("password", Environment.PASS);
        try (Connection conn = DriverManager.getConnection(Environment.DB_URL, dbProperties)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            statement.setString(1, userLoginDetails.getEmail());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("No such user");
                }

                String hash = rs.getString("hash");
                String salt = rs.getString("salt");
                if (!HashUtils.verify(userLoginDetails.getPassword(), salt, hash)) {
                    throw new Exception("Incorrect password");
                }

                String validatedAuthKey = validateAndUpdateAuthKey(rs.getString("authKey"),
                        rs.getString("email"));

                PublicUserDetails userToReturn = new PublicUserDetails();
                userToReturn.setUserName(rs.getString("userName"));
                userToReturn.setFirstName(rs.getString("firstName"));
                userToReturn.setLastName(rs.getString("lastName"));
                userToReturn.setEmail(rs.getString("email"));
                userToReturn.setDob(rs.getString("dob"));
                userToReturn.setAuthKey(validatedAuthKey);
                userToReturn.setRegistrationDate(rs.getString("registrationDate"));

                Blob avatarAsBlob = rs.getBlob("avatar");
                if (avatarAsBlob != null) {
                    byte[] avatarBytes = avatarAsBlob.getBytes(1, (int) avatarAsBlob.length());
                    String avatarAsBase64String = Base64.getEncoder().encodeToString(avatarBytes);
                    userToReturn.setUserAvatar(avatarAsBase64String);
                }
                return userToReturn;
            }
        }
    }

    public boolean insertUser(User userToInsert) throws SQLException, NoSuchAlgorithmException {
        dbProperties.put("user", Environment.USER);
        dbProperties.put("password", Environment.PASS);
        String salt = HashUtils.generateSalt();
        String hash = HashUtils.hashWithSalt(userToInsert.getPassword(), salt);
        String authKey = HashUtils.hashWithSalt(userToInsert.getEmail(), salt);
        
        try (Connection conn = DriverManager.getConnection(Environment.DB_URL, dbProperties)) {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO users (userName, firstName, lastName, email, hash, salt, authKey, authKeyExpiry, dob, avatar, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, userToInsert.getUserName());
            statement.setString(2, userToInsert.getFirstName());
            statement.setString(3, userToInsert.getLastName());
            statement.setString(4, userToInsert.getEmail());
            statement.setString(5, hash);
            statement.setString(6, salt);
            statement.setString(7, authKey);
            statement.setString(8, TimeUtils.getDateNowPlus6MonthsAsISODateTimeString());
            statement.setString(9, userToInsert.getDob());

            if (userToInsert.getAvatar() != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(userToInsert.getAvatar());
                Blob avatarBlob = conn.createBlob();
                avatarBlob.setBytes(1, decodedBytes);
                statement.setBlob(10, avatarBlob);
            } else {
                statement.setNull(10, Types.BLOB);
            }

            statement.setString(11, userToInsert.getRegistrationDate());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
    }
}
