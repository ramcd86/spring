package services.storemanagement;

import com.hellokoding.springboot.view.storeclasses.Store;
import com.hellokoding.springboot.view.storeclasses.StoreItem;
import com.hellokoding.springboot.view.storeclasses.StoreSummary;
import com.hellokoding.springboot.view.storeclasses.StoreSummaryResponse;
import org.springframework.stereotype.Service;
import services.utils.DatabaseVerification;
import services.utils.HashUtils;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
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
                                "storeAvatar, " +
                                "storeBanner) VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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

            if (store.getStoreAvatar() != null) {
                statement.setBinaryStream(13, inputStream, inputStream.available());
            } else {
                statement.setNull(13, Types.BLOB);
            }

            if (store.getStoreBanner() != null) {
                statement.setBinaryStream(14, inputStream, inputStream.available());
            } else {
                statement.setNull(14, Types.BLOB);
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

    public StoreSummaryResponse getStoresListSummaryFromDatabase() throws SQLException {
        StoreSummaryResponse response = new StoreSummaryResponse();
        response.setStoreSummaryQueryStatus(StoreEnums.STORE_LIST_EMPTY);
        List<StoreSummary> storeSummaries = new ArrayList<StoreSummary>();
        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT storeTitle, storeDescription, storeTheme, ownUUID, storeBanner FROM stores");
                ResultSet rs = statement.executeQuery();
        ) {
            if (!rs.next()) {
                return response;
            }

            while (rs.next()) {
                response.setStoreSummaryQueryStatus(StoreEnums.STORE_LIST_POPULATED);
                StoreSummary storeSummary = new StoreSummary();
                String storeTitle = rs.getString("storeTitle");
                String storeDescription = rs.getString("storeDescription");
                String ownUUID = rs.getString("ownUUID");
                String storeTheme = rs.getString("storeTheme");

                storeSummary.setStoreTitle(storeTitle);
                storeSummary.setStoreDescription(storeDescription);
                storeSummary.setOwnUUID(ownUUID);
                storeSummary.setStoreTheme(storeTheme);
                storeSummaries.add(storeSummary);
            }

            response.setStores(storeSummaries);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public byte[] getStoreImage(String imageType, String associatedStoreUUID) throws SQLException {

        byte[] image;
        String storeImageQueryType = "";

        if (Objects.equals(imageType, "avatar")) {
            storeImageQueryType = "storeAvatar";
        }
        if (Objects.equals(imageType, "banner")) {
            storeImageQueryType = "storeBanner";
        }

        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT " + storeImageQueryType + " FROM stores WHERE ownUUID = ?");
        ) {

            statement.setString(1, associatedStoreUUID);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("No such image");
            }


            Blob imageData = rs.getBlob(storeImageQueryType);
            image = imageData.getBytes(1, (int) imageData.length());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

}
