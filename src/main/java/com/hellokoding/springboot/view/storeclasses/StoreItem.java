package com.hellokoding.springboot.view.storeclasses;

public class StoreItem {
    private String storeItemName;
    private String storeItemImage;
    private String storeItemDescription;
    private String storeParentUUID;

    private String storeItemPrice;

    public String getStoreItemName() {
        return storeItemName;
    }

    public void setStoreItemName(String storeItemName) {
        this.storeItemName = storeItemName;
    }

    public String getStoreItemImage() {
        return storeItemImage;
    }

    public void setStoreItemImage(String storeItemImage) {
        this.storeItemImage = storeItemImage;
    }

    public String getStoreItemDescription() {
        return storeItemDescription;
    }

    public void setStoreItemDescription(String storeItemDescription) {
        this.storeItemDescription = storeItemDescription;
    }

    public String getStoreParentUUID() {
        return storeParentUUID;
    }

    public void setStoreParentUUID(String storeParentUUID) {
        this.storeParentUUID = storeParentUUID;
    }


    @Override
    public String toString() {
        return "StoreItem{" +
                "storeItemName='" + storeItemName + '\'' +
                ", storeItemImage='" + storeItemImage + '\'' +
                ", storeItemDescription='" + storeItemDescription + '\'' +
                ", storeParentUUID='" + storeParentUUID + '\'' +
                ", storeItemPrice='" + storeItemPrice + '\'' +
                '}';
    }

    public String getStoreItemPrice() {
        return storeItemPrice;
    }

    public void setStoreItemPrice(String storeItemPrice) {
        this.storeItemPrice = storeItemPrice;
    }
}
