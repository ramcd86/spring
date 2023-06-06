package com.hellokoding.springboot.view;

import com.hellokoding.springboot.view.storeclasses.Store;
import com.hellokoding.springboot.view.storeclasses.StoreResponse;
import com.hellokoding.springboot.view.storeclasses.StoreSummaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import services.storemanagement.StoreManagementService;
import services.utils.ImageProcessorService;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Controller
public class StoreController {

    private final StoreManagementService storeManagementService;
    private final ImageProcessorService imageProcessingService;

    public StoreController(StoreManagementService storeManagementService, ImageProcessorService imageProcessingService) {
        this.storeManagementService = storeManagementService;
        this.imageProcessingService = imageProcessingService;
    }

    @PostMapping("create-store")
    public ResponseEntity<String> insertStore(@RequestBody Store storeToBeInserted) {

        CompletableFuture<StoreEnums> insertUserQuery = CompletableFuture.supplyAsync(() -> {
            try {
                return storeManagementService.insertStore(storeToBeInserted);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        StoreEnums storeRegistrationStatus = insertUserQuery.join();

        LoggingUtils.log(storeRegistrationStatus);

        switch (storeRegistrationStatus) {
            case INVALID_UUID:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid UUID.");
            case INSERTION_FAILED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown Error.");
            case STORE_INSERTED:
                return ResponseEntity.status(HttpStatus.OK).body("Store inserted.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown Error.");
    }

    @GetMapping("get-stores-list")
    public ResponseEntity<StoreSummaryResponse> getStoresListSummary() {

        StoreSummaryResponse storeSummaryResponse;

        CompletableFuture<StoreSummaryResponse> storesListSummary = CompletableFuture.supplyAsync(() -> {
            try {
                return storeManagementService.getStoresListSummaryFromDatabase();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        storeSummaryResponse = storesListSummary.join();


        return ResponseEntity.ok(storeSummaryResponse);
    }

    @GetMapping("get-store/{storeId}")
    public ResponseEntity<StoreResponse> getIndividualStore(@PathVariable("storeId") String storeId) {

        StoreResponse storeResponse;

        CompletableFuture<StoreResponse> storeSummaryResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return storeManagementService.getIndividualStore(storeId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        storeResponse = storeSummaryResponseCompletableFuture.join();


        return ResponseEntity.ok(storeResponse);
    }


    @GetMapping("store-banner-images/{imageType}/{storeUUID}.{fileType}")
    public ResponseEntity<?> getStoreBannerImage(@PathVariable("imageType") String imageType,
                                                 @PathVariable("storeUUID") String storeUUID,
                                                 @PathVariable("fileType") String filetype) {

        byte[] storeImage;

        CompletableFuture<byte[]> storeImageCompletable = CompletableFuture.supplyAsync(() -> {
            try {
                return imageProcessingService.getStoreImage(imageType, storeUUID);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        storeImage = storeImageCompletable.join();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/" + filetype))
                .body(storeImage);
    }

}
