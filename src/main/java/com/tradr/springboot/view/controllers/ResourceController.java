package com.tradr.springboot.view.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import services.utils.ImageProcessorService;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Controller
public class ResourceController {

    private final ImageProcessorService imageProcessingService;

    public ResourceController(ImageProcessorService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
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
