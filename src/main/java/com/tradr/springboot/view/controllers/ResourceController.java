package com.tradr.springboot.view.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import services.utils.ImageProcessorService;

@Controller
public class ResourceController {

    private final ImageProcessorService imageProcessingService;

    public ResourceController(ImageProcessorService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @GetMapping("store-images/{imageType}/{storePublicId}.{fileType}")
    public ResponseEntity<?> getStoreImage(@PathVariable("imageType") String imageType,
                                           @PathVariable("storePublicId") String storePublicId,
                                           @PathVariable("fileType") String filetype) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/" + filetype))
                .body(imageProcessingService.getStoreImage(imageType, storePublicId));
    }

    @GetMapping("store-item-image/{storeItemPublicId}.{fileType}")
    public ResponseEntity<?> getStoreItemImage(
            @PathVariable("storeItemPublicId") String storeItemPublicId,
            @PathVariable("fileType") String filetype) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/" + filetype))
                .body(imageProcessingService.getStoreItemImage(storeItemPublicId));
    }

//    @GetMapping("user-images/{imageType}/{storeUUID}.{fileType}")
//    public ResponseEntity<?> getUserImage(@PathVariable("imageType") String imageType,
//                                          @PathVariable("storeUUID") String storeUUID,
//                                          @PathVariable("fileType") String filetype) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/" + filetype))
//                .body(imageProcessingService.getStoreImage(imageType, storeUUID));
//    }

}
