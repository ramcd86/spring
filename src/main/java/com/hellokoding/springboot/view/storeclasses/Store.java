package com.hellokoding.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Store {
    private String storeTitle;
    private String storeDescription;
    private boolean canMessage;
    private boolean isPrivate;
    private String storeTheme;
    private List<StoreItem> storeItems;
    private List<String> craftTags;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String postcode;
    private String parentUUID;
    private String ownUUID;
    private String publicStoreId;
    private String storeAvatar;
    private String storeBanner;
}

