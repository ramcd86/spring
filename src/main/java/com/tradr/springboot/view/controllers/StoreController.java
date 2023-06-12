package com.tradr.springboot.view.controllers;

import com.tradr.springboot.view.storeclasses.*;
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
import services.utils.LoggingUtils;
import services.utils.StoreEnums;

@Controller
public class StoreController {

    private final StoreManagementService storeManagementService;
    private final UserManagementService userManagementService;

    public StoreController(StoreManagementService storeManagementService, UserManagementService userManagementService) {
        this.storeManagementService = storeManagementService;
        this.userManagementService = userManagementService;
    }
    
    @PostMapping("create-store")
    public ResponseEntity<StoreEnums> insertStore(@RequestBody Store storeToBeInserted) {
        StoreEnums resultFromService = storeManagementService.insertStore(storeToBeInserted, userManagementService);

        switch (resultFromService) {
            case INVALID_UUID:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(StoreEnums.INVALID_UUID);
            case INSERTION_FAILED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.INSERTION_FAILED);
            case STORE_INSERTED:
                return ResponseEntity.status(HttpStatus.OK).body(StoreEnums.STORE_INSERTED);
            case STORE_CREATION_FAILED_STORE_EXISTS:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(StoreEnums.STORE_CREATION_FAILED_STORE_EXISTS);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.INSERTION_FAILED);
    }

    @GetMapping("get-stores-list")
    public ResponseEntity<StoreSummaryResponse> getStoresListSummary() {
        StoreSummaryResponse storeSummaryResponse = storeManagementService.getStoresListSummaryFromDatabase();
        return ResponseEntity.ok(storeSummaryResponse);
    }

    @GetMapping("get-store/{storeId}")
    public ResponseEntity<StoreResponse> getIndividualStore(@PathVariable("storeId") String storeId) {
        StoreResponse storeResponse = storeManagementService.getIndividualStore(storeId);
        return ResponseEntity.ok(storeResponse);
    }

    @PostMapping("insert-item")
    public ResponseEntity<StoreEnums> insertItemIntoExistingStore(@RequestBody StoreItemInsert itemToBeInserted) {

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

    @PostMapping("delete-item")
    public ResponseEntity<StoreEnums> deleteItemFromStore(@RequestBody StoreItemDeletion itemToBeDeleted) {

        LoggingUtils.log(itemToBeDeleted.toString());

        UserAuthKey authKey = new UserAuthKey();
        authKey.setAuthKey(itemToBeDeleted.getAuthKey());

        if (!userManagementService.isAuthKeyValid(authKey)) {
            return ResponseEntity.badRequest().body(StoreEnums.ITEM_DELETION_FAILED);
        }

        StoreEnums itemInsertionState = storeManagementService.deleteItem(itemToBeDeleted.getStoreItemPublicId(), itemToBeDeleted.getAuthKey(), userManagementService);

        if (itemInsertionState == StoreEnums.ITEM_DELETED) {
            return ResponseEntity.status(HttpStatus.OK).body(StoreEnums.ITEM_DELETED);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.ITEM_DELETION_FAILED);
    }

    @PostMapping("delete-store")
    public ResponseEntity<StoreEnums> deleteStore(@RequestBody UserAuthKey authKey) {

        if (!userManagementService.isAuthKeyValid(authKey)) {
            return ResponseEntity.badRequest().body(StoreEnums.STORE_DELETION_FAILED);
        }

        String userOwnedStoreUUID = userManagementService.getUserOwnedStoreUUID(authKey.getAuthKey());
        StoreEnums itemInsertionState = storeManagementService.deleteStore(authKey, userOwnedStoreUUID);

        if (itemInsertionState == StoreEnums.STORE_DELETION_SUCCESSFUL) {
            return ResponseEntity.status(HttpStatus.OK).body(StoreEnums.STORE_DELETION_SUCCESSFUL);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StoreEnums.STORE_DELETION_FAILED);
    }

}
