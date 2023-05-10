package com.hellokoding.springboot.view.storeclasses;

import java.util.List;


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
    private List<StoreReviews> storeReviews;
    private String storeBanner;

    public String getStoreTitle() {
        return storeTitle;
    }

    public void setStoreTitle(String storeTitle) {
        this.storeTitle = storeTitle;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public boolean isCanMessage() {
        return canMessage;
    }

    public void setCanMessage(boolean canMessage) {
        this.canMessage = canMessage;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getStoreTheme() {
        return storeTheme;
    }

    public void setStoreTheme(String storeTheme) {
        this.storeTheme = storeTheme;
    }

    public List<StoreItem> getStoreItems() {
        return storeItems;
    }

    public void setStoreItems(List<StoreItem> storeItems) {
        this.storeItems = storeItems;
    }

    public List<String> getCraftTags() {
        return craftTags;
    }

    public void setCraftTags(List<String> craftTags) {
        this.craftTags = craftTags;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }

    public String getOwnUUID() {
        return ownUUID;
    }

    public void setOwnUUID(String ownUUID) {
        this.ownUUID = ownUUID;
    }

    public String getStoreBanner() {
        return storeBanner;
    }

    public void setStoreBanner(String storeBanner) {
        this.storeBanner = storeBanner;
    }

    public List<StoreReviews> getStoreReviews() {
        return storeReviews;
    }

    public void setStoreReviews(List<StoreReviews> storeReviews) {
        this.storeReviews = storeReviews;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeTitle='" + storeTitle + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", canMessage=" + canMessage +
                ", isPrivate=" + isPrivate +
                ", storeTheme='" + storeTheme + '\'' +
                ", storeItems=" + storeItems +
                ", craftTags=" + craftTags +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", addressLine3='" + addressLine3 + '\'' +
                ", postcode='" + postcode + '\'' +
                ", parentUUID='" + parentUUID + '\'' +
                ", ownUUID='" + ownUUID + '\'' +
                ", storeReviews=" + storeReviews +
                ", storeBanner='" + storeBanner + '\'' +
                '}';
    }


    //getters and setters for all fields go here
}

