package services.registration;

import com.tradr.springboot.view.userclasses.*;
import org.springframework.stereotype.Service;
import services.utils.DatabaseVerification;
import services.utils.HashUtils;
import services.utils.TimeUtils;
import services.utils.UserEnums;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;


@Service
public class UserManagementService {

    public boolean isAuthKeyValid(UserAuthKey authKey) {

        CompletableFuture<Boolean> isAuthKeyValidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "SELECT * FROM users WHERE authKey = '" + authKey.getAuthKey() + "'");
            ) {

                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    return false;
                }
                String authKeyTimeStamp = rs.getString("authKeyExpiry");
                return !TimeUtils.isISOStringOutOfDate(authKeyTimeStamp);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return isAuthKeyValidCompletableFuture.join();

    }

    private String updateAuthKey(String email) throws Exception {

        String salt = HashUtils.generateSalt();
        String authKey = HashUtils.hashWithSalt(email, salt);

        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "UPDATE users SET authKey = '" + authKey + "', authKeyExpiry = '"
                                + TimeUtils.getDateNowPlus6MonthsAsISODateTimeString() + "' WHERE email = '" + email
                                + "'");
        ) {

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

    public PublicUserDetailsResponse getUser(UserLoginDetails userLoginDetails) {

        PublicUserDetailsResponse publicUserDetailsResponse = new PublicUserDetailsResponse();
        publicUserDetailsResponse.setPublicUserQueryResponseStatus(UserEnums.LOGIN_FAILED);

        CompletableFuture<PublicUserDetailsResponse> getUserCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            ) {

                PublicUserDetailsResponse publicUserDetailsResponseFinal = new PublicUserDetailsResponse();

                statement.setString(1, userLoginDetails.getEmail());
                ResultSet rs = statement.executeQuery();
                if (!rs.next()) {
                    publicUserDetailsResponseFinal.setPublicUserQueryResponseStatus(UserEnums.NO_SUCH_USER);
                    return publicUserDetailsResponseFinal;
                }

                String hash = rs.getString("hash");
                String salt = rs.getString("salt");
                if (!HashUtils.verify(userLoginDetails.getPassword(), salt, hash)) {
                    publicUserDetailsResponseFinal.setPublicUserQueryResponseStatus(UserEnums.INCORRECT_PASSWORD);
                    return publicUserDetailsResponseFinal;
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
                userToReturn.setUuid(rs.getString("uuid"));

                Blob avatarAsBlob = rs.getBlob("avatar");
                if (avatarAsBlob != null) {
                    byte[] avatarBytes = avatarAsBlob.getBytes(1, (int) avatarAsBlob.length());
                    String avatarAsBase64String = Base64.getEncoder().encodeToString(avatarBytes);
                    userToReturn.setUserAvatar(avatarAsBase64String);
                }

                publicUserDetailsResponseFinal.setPublicUserQueryResponseStatus(UserEnums.LOGIN_SUCCESSFUL);
                publicUserDetailsResponseFinal.setPublicUserDetails(userToReturn);

                return publicUserDetailsResponseFinal;

            } catch (SQLException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        publicUserDetailsResponse = getUserCompletableFuture.join();

        return publicUserDetailsResponse;
    }

    public boolean userExists(String email) {

        CompletableFuture<Boolean> userExistCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            ) {
                statement.setString(1, email);
                ResultSet rs = statement.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return userExistCompletableFuture.join();
    }

    public boolean insertUser(User userToInsert) {

        CompletableFuture<Boolean> insertUserCompletableFuture = CompletableFuture.supplyAsync(() -> {

            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "INSERT INTO users (userName, firstName, lastName, email, hash, salt, authKey, authKeyExpiry, dob, avatar, uuid, ownedStoreUUID, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ) {
                String salt = HashUtils.generateSalt();
                String hash = HashUtils.hashWithSalt(userToInsert.getPassword(), salt);
                String authKey = HashUtils.hashWithSalt(userToInsert.getEmail(), salt);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(userToInsert.getAvatar()));
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
                    statement.setBinaryStream(10, inputStream, inputStream.available());
                } else {
                    statement.setNull(10, Types.BLOB);
                }

                statement.setString(11, HashUtils.generateUUID());
                statement.setString(12, "null");
                statement.setString(13, userToInsert.getRegistrationDate());

                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        return insertUserCompletableFuture.join();

    }

    public boolean updateUserOwnedStoreUUID(String ownedStoreUUID, String ownUUID) {

        CompletableFuture<Boolean> updateUserOwnedStoreUUIDCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "UPDATE users SET ownedStoreUUID = '" + ownedStoreUUID + "' WHERE uuid = '" + ownUUID + "'");
            ) {

                int rs = statement.executeUpdate();
                return rs > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return updateUserOwnedStoreUUIDCompletableFuture.join();
    }

    public String getUserOwnedStoreUUID(String authKey) {

        CompletableFuture<String> getUserChildStoreUUIDCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "SELECT * FROM users WHERE authKey = ?;");
            ) {
                statement.setString(1, authKey);
                ResultSet rs = statement.executeQuery();
                String ownedStoreUUID = "";

                if (!rs.next()) {
                    return ownedStoreUUID;
                }

                do {
                    ownedStoreUUID = rs.getString("ownedStoreUUID");
                } while (rs.next());

                return ownedStoreUUID;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return getUserChildStoreUUIDCompletableFuture.join();

    }

    ;


}
