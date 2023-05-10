package services.storemanagement;

import com.hellokoding.springboot.view.storeclasses.Store;
import com.hellokoding.springboot.view.storeclasses.StoreItem;
import org.springframework.stereotype.Service;
import services.utils.DatabaseVerification;
import services.utils.HashUtils;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StoreManagementService {

    public boolean isUUIDValid(String uuid) throws Exception, SQLException {
        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT * FROM users WHERE uuid = '" + uuid + "'");
                ResultSet rs = statement.executeQuery()
        ) {
            if (!rs.next()) {
                throw new Exception("No such UUID key");
            }
            return true;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public StoreEnums insertStore(Store store) throws SQLException {

        CompletableFuture<Boolean> validateUUID = CompletableFuture.supplyAsync(() -> {
            try {
                return isUUIDValid(store.getParentUUID());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (!validateUUID.join()) {
            return StoreEnums.INVALID_UUID;
        }

        String craftTagsAsString = String.join(",", store.getCraftTags());
        String storeOwnUUD = HashUtils.generateUUID();


        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement(
                        "INSERT INTO stores (" +
                                "storeTitle, " +
                                "storeDescription, " +
                                "canMessage, " +
                                "isPrivate, " +
                                "storeTheme, " +
                                "craftTags, " +
                                "addressLine1, " +
                                "addressLine2, " +
                                "addressLine3, " +
                                "postcode, " +
                                "parentUUID, " +
                                "ownUUID, " +
                                "storeBanner) VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ) {


            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(store.getStoreBanner()));
            statement.setString(1, store.getStoreTitle());
            statement.setString(2, store.getStoreDescription());
            statement.setBoolean(3, store.isCanMessage());
            statement.setBoolean(4, store.isPrivate());
            statement.setString(5, store.getStoreTheme());
            statement.setString(6, craftTagsAsString);
            statement.setString(7, store.getAddressLine1());
            statement.setString(8, store.getAddressLine2());
            statement.setString(9, store.getAddressLine3());
            statement.setString(10, store.getPostcode());
            statement.setString(11, store.getParentUUID());
            statement.setString(12, storeOwnUUD);

            if (store.getStoreBanner() != null) {
                statement.setBinaryStream(13, inputStream, inputStream.available());
            } else {
                statement.setNull(13, Types.BLOB);
            }

            //                    "    storeItems TEXT(20000) NOT NULL,\n" +
            //                    "    storeReviews TEXT(30000) NOT NULL,\n" +

            CompletableFuture<StoreEnums> insertStoreItems = CompletableFuture.supplyAsync(() -> {
                try {
                    return prepareStoreItemsForInsertion(store.getStoreItems(), storeOwnUUD);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            StoreEnums insertStoreItemsJoined = insertStoreItems.join();

            int rowsInserted = statement.executeUpdate();
            return (rowsInserted > 0 && insertStoreItemsJoined == StoreEnums.ITEM_INSERTED) ? StoreEnums.STORE_INSERTED : StoreEnums.INSERTION_FAILED;

        } catch (SQLException e) {
            LoggingUtils.log(e);
            return StoreEnums.INSERTION_FAILED;
        }

    }

    public StoreEnums prepareStoreItemsForInsertion(List<StoreItem> storeItemsList, String parentUUID) throws SQLException {

        for (StoreItem storeItem : storeItemsList) {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "INSERT INTO storeitems (storeItemName, parentUUID, storeItemImage, storeItemDescription, storeItemPrice) VALUES " +
                                    "(?, ?, ?, ?, ?)");
            ) {

                LoggingUtils.log(storeItem.toString());

                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(storeItem.getStoreItemImage()));
                statement.setString(1, storeItem.getStoreItemName());
                statement.setString(2, parentUUID);
                if (storeItem.getStoreItemImage() != null) {
                    statement.setBinaryStream(3, inputStream, inputStream.available());
                } else {
                    statement.setNull(3, Types.BLOB);
                }
                statement.setString(4, storeItem.getStoreItemDescription());
                statement.setString(5, storeItem.getStoreItemPrice());
                int rowsInserted = statement.executeUpdate();
                return (rowsInserted > 0) ? StoreEnums.ITEM_INSERTED : StoreEnums.ITEM_INSERTION_FAILED;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return StoreEnums.ITEM_INSERTION_FAILED;
    }

}
