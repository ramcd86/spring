package services.utils;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageProcessorService {

    public byte[] getStoreImage(String imageType, String storePublicId) {

        CompletableFuture<byte[]> getStoreImageCompletableFuture = CompletableFuture.supplyAsync(() -> {

            String storeImageQueryType = "";

            if (Objects.equals(imageType, "avatar")) {
                storeImageQueryType = "storeAvatar";
            }
            if (Objects.equals(imageType, "banner")) {
                storeImageQueryType = "storeBanner";
            }

            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement("SELECT " + storeImageQueryType + " FROM stores WHERE publicStoreId = ?");
            ) {

                statement.setString(1, storePublicId);
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    throw new RuntimeException("No such image");
                }

                Blob imageData = rs.getBlob(storeImageQueryType);

                return imageData.getBytes(1, (int) imageData.length());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        return getStoreImageCompletableFuture.join();
    }

    public byte[] getStoreItemImage(String storeItemPublicId) {

        CompletableFuture<byte[]> getStoreItemImageCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try (
                    Connection conn = DatabaseVerification.getConnection();
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM storeItems WHERE storeItemPublicId = ?");
            ) {

                statement.setString(1, storeItemPublicId);
                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    throw new RuntimeException("No such image");
                }

                Blob imageData = rs.getBlob("storeItemImage");

                return imageData.getBytes(1, (int) imageData.length());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        return getStoreItemImageCompletableFuture.join();
    }
}
