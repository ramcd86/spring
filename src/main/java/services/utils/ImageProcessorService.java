package services.utils;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Objects;

@Service
public class ImageProcessorService {

    public byte[] getStoreImage(String imageType, String publicStoreId) throws SQLException {

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
                PreparedStatement statement = conn.prepareStatement("SELECT " + storeImageQueryType + " FROM stores WHERE publicStoreId = ?");
        ) {

            statement.setString(1, publicStoreId);
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
