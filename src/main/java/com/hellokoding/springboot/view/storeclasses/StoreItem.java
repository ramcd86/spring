package com.hellokoding.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreItem {
    private String storeItemName;
    private String storeItemImage;
    private String storeItemDescription;
    private String storeParentUUID;
    private String storeItemPrice;
}
