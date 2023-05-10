package com.hellokoding.springboot.view;

import com.hellokoding.springboot.view.storeclasses.Store;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import services.storemanagement.StoreManagementService;
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Controller
public class StoreController {

    private final StoreManagementService storeManagementService;

    public StoreController(StoreManagementService storeManagementService) {
        this.storeManagementService = storeManagementService;
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


}
