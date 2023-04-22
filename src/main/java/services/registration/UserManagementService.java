package services.registration;

import com.hellokoding.springboot.view.userclasses.PublicUserDetails;
import com.hellokoding.springboot.view.userclasses.User;
import com.hellokoding.springboot.view.userclasses.UserLoginDetails;
import org.springframework.stereotype.Service;
import services.utils.Environment;
import services.utils.HashUtils;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;

@Service
public class UserManagementService {
    private final Properties dbProperties = new Properties();

    public PublicUserDetails getUser(UserLoginDetails userLoginDetails) throws Exception {
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

                PublicUserDetails userToReturn = new PublicUserDetails();
                userToReturn.setUserName(rs.getString("userName"));
                userToReturn.setFirstName(rs.getString("firstName"));
                userToReturn.setLastName(rs.getString("lastName"));
                userToReturn.setEmail(rs.getString("email"));
                userToReturn.setDob(rs.getString("dob"));
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
        String salt = HashUtils.generateSalt();
        String hash = HashUtils.hashWithSalt(userToInsert.getPassword(), salt);

        try (Connection conn = DriverManager.getConnection(Environment.DB_URL, dbProperties)) {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (userName, firstName, lastName, email, hash, salt, dob, loginHash, authKey, authKeyExpiry, avatar, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, userToInsert.getUserName());
            statement.setString(2, userToInsert.getFirstName());
            statement.setString(3, userToInsert.getLastName());
            statement.setString(4, userToInsert.getEmail());
            statement.setString(5, hash);
            statement.setString(6, salt);
            statement.setString(7, userToInsert.getDob());
            statement.setString(8, "0");
            statement.setString(9, userToInsert.getDob());
            statement.setString(10, "0");

            if (userToInsert.getAvatar() != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(userToInsert.getAvatar());
                Blob avatarBlob = conn.createBlob();
                avatarBlob.setBytes(1, decodedBytes);
                statement.setBlob(11, avatarBlob);
            } else {
                statement.setNull(11, Types.BLOB);
            }

            statement.setDate(12, new java.sql.Date(userToInsert.getRegistrationDate().toEpochDay()));

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
    }
}
