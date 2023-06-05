package com.hellokoding.springboot.view.storeclasses;

public class StoreSummary {

    private String storeTitle;
    private String storeDescription;
    private String storeTheme;
    private String ownUUID;

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

    public String getStoreTheme() {
        return storeTheme;
    }

    public void setStoreTheme(String storeTheme) {
        this.storeTheme = storeTheme;
    }

    public String getOwnUUID() {
        return ownUUID;
    }

    public void setOwnUUID(String ownUUID) {
        this.ownUUID = ownUUID;
    }


    @Override
    public String toString() {
        return "storeSummary{" +
                "storeTitle='" + storeTitle + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", storeTheme='" + storeTheme + '\'' +
                ", ownUUID='" + ownUUID + '\'' +
                '}';
    }
}
