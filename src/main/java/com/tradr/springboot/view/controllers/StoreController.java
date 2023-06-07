package com.tradr.springboot.view.controllers;

import com.tradr.springboot.view.storeclasses.Store;
import com.tradr.springboot.view.storeclasses.StoreItemInsert;
import com.tradr.springboot.view.storeclasses.StoreResponse;
import com.tradr.springboot.view.storeclasses.StoreSummaryResponse;
import com.tradr.springboot.view.userclasses.UserAuthKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import services.registration.UserManagementService;
import services.storemanagement.StoreManagementService;
import services.utils.StoreEnums;

import java.sql.SQLException;

@Controller
public class StoreController {

    private final StoreManagementService storeManagementService;
    private final UserManagementService userManagementService;

    public StoreController(StoreManagementService storeManagementService, UserManagementService userManagementService) {
        this.storeManagementService = storeManagementService;
        this.userManagementService = userManagementService;
    }

    @PostMapping("create-store")
    public ResponseEntity<StoreEnums> insertStore(@RequestBody Store storeToBeInserted) throws SQLException {
        StoreEnums resultFromService = storeManagementService.insertStore(storeToBeInserted);

        switch (resultFromService) {
            case INVALID_UUID:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(StoreEnums.INVALID_UUID);
            case INSERTION_FAILED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.INSERTION_FAILED);
            case STORE_INSERTED:
                return ResponseEntity.status(HttpStatus.OK).body(StoreEnums.STORE_INSERTED);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.INSERTION_FAILED);
    }

    @PostMapping("insert-item")
    public ResponseEntity<StoreEnums> insertItemIntoExistingStore(@RequestBody StoreItemInsert itemToBeInserted) throws SQLException {

        UserAuthKey authKey = new UserAuthKey();
        authKey.setAuthKey(itemToBeInserted.getAuthKey());

        if (!userManagementService.isAuthKeyValid(authKey)) {
            return ResponseEntity.badRequest().body(StoreEnums.ITEM_INSERTION_FAILED);
        }

        StoreEnums itemInsertionState = storeManagementService.prepareStoreItemsForInsertion(itemToBeInserted.getStoreItem(), itemToBeInserted.getParentUUID());

        if (itemInsertionState == StoreEnums.ITEM_INSERTED) {
            return ResponseEntity.status(HttpStatus.OK).body(StoreEnums.ITEM_INSERTED);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.ITEM_INSERTION_FAILED);
    }

    @GetMapping("get-stores-list")
    public ResponseEntity<StoreSummaryResponse> getStoresListSummary() throws SQLException {
        StoreSummaryResponse storeSummaryResponse = storeManagementService.getStoresListSummaryFromDatabase();
        return ResponseEntity.ok(storeSummaryResponse);
    }

    @GetMapping("get-store/{storeId}")
    public ResponseEntity<StoreResponse> getIndividualStore(@PathVariable("storeId") String storeId) throws SQLException {
        StoreResponse storeResponse = storeManagementService.getIndividualStore(storeId);
        return ResponseEntity.ok(storeResponse);
    }

}