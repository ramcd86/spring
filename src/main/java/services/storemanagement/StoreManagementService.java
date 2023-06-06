package services.storemanagement;

import com.hellokoding.springboot.view.storeclasses.*;
import org.springframework.stereotype.Service;
import services.utils.DatabaseVerification;
import services.utils.HashUtils;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
                                "publicStoreId, " +
                                "storeAvatar, " +
                                "storeBanner) VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            statement.setString(13, HashUtils.generatePublicId(store.getStoreTitle()));

            if (store.getStoreAvatar() != null) {
                statement.setBinaryStream(14, inputStream, inputStream.available());
            } else {
                statement.setNull(14, Types.BLOB);
            }

            if (store.getStoreBanner() != null) {
                statement.setBinaryStream(15, inputStream, inputStream.available());
            } else {
                statement.setNull(15, Types.BLOB);
            }

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
            return StoreEnums.INSERTION_FAILED;
        }

    }

    public StoreEnums prepareStoreItemsForInsertion(List<StoreItem> storeItemsList, String parentUUID) throws SQLException {

        for (StoreItem storeItem : storeItemsList) {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement(
                            "INSERT INTO storeitems (storeItemName, parentUUID, storeItemImage, storeItemDescription, storeItemPrice, storeItemPublicId) VALUES " +
                                    "(?, ?, ?, ?, ?, ?)");
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
                statement.setString(6, HashUtils.generatePublicId(storeItem.getStoreItemName()));
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
                PreparedStatement statement = conn.prepareStatement("SELECT storeTitle, storeDescription, storeTheme, ownUUID, storeBanner, publicStoreId FROM stores");
                ResultSet rs = statement.executeQuery();
        ) {
            if (!rs.next()) {
                return response;
            }

            do {
                response.setStoreSummaryQueryStatus(StoreEnums.STORE_LIST_POPULATED);
                StoreSummary storeSummary = new StoreSummary();
                String storeTitle = rs.getString("storeTitle");
                String storeDescription = rs.getString("storeDescription");
                String ownUUID = rs.getString("ownUUID");
                String storeTheme = rs.getString("storeTheme");
                String publicStoreId = rs.getString("publicStoreId");

                storeSummary.setStoreTitle(storeTitle);
                storeSummary.setStoreDescription(storeDescription);
                storeSummary.setOwnUUID(ownUUID);
                storeSummary.setStoreTheme(storeTheme);
                storeSummary.setPublicStoreId(publicStoreId);
                storeSummaries.add(storeSummary);
            } while (rs.next());

            response.setStores(storeSummaries);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return response;
    }


    public StoreResponse getIndividualStore(String storeId) throws SQLException {

        StoreResponse response = new StoreResponse();
        response.setStoreQueryResponseStatus(StoreEnums.STORE_EMPTY);

        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM stores WHERE publicStoreId = ?");
        ) {

            statement.setString(1, storeId);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("No such store");
            }

            Store store = new Store();
            response.setStoreQueryResponseStatus(StoreEnums.STORE_POPULATED);
            String[] craftingTags = rs.getString("craftingTags").split(",");
            List<String> craftingTagsAsList = new ArrayList<>(Arrays.asList(craftingTags));
            List<StoreItem> storeItems = getStoreItemsForIndividualStore(rs.getString("ownUUID"));

            store.setStoreTitle(rs.getString("storeTitle"));
            store.setStoreDescription(rs.getString("storeDescription"));
            store.setCanMessage(rs.getBoolean("canMessage"));
            store.setPrivate(rs.getBoolean("isPrivate"));
            store.setStoreTheme(rs.getString("storeTheme"));
            store.setCraftTags(craftingTagsAsList);
            store.setAddressLine1(rs.getString("addressLine1"));
            store.setAddressLine2(rs.getString("addressLine2"));
            store.setAddressLine3(rs.getString("addressLine3"));
            store.setPostcode(rs.getString("postcode"));
            store.setParentUUID(rs.getString("parentUUID"));
            store.setOwnUUID(rs.getString("ownUUID"));
            store.setPublicStoreId(rs.getString("publicStoreID"));


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private List<StoreItem> getStoreItemsForIndividualStore(String parentUUID) {

        List<StoreItem> storeItems = new ArrayList<StoreItem>();

        try (
                Connection conn = DatabaseVerification.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM storeItems WHERE parentUUID = ?");
        ) {

            statement.setString(1, parentUUID);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("No such items");
            }

            do {
                StoreItem storeItem = new StoreItem();
                storeItem.setStoreItemName(rs.getString("storeItemName"));
                storeItem.setStoreParentUUID(rs.getString("parentUUID"));
                storeItem.setStoreItemDescription(rs.getString("storeItemDescription"));
                storeItem.setStoreItemPrice(rs.getString("storeItemPrice"));
                storeItem.setStoreItemPublicId(rs.getString("storeItemPublicId"));
                storeItems.add(storeItem);
            } while (rs.next());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return storeItems;
    }

}
