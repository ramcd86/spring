package com.tradr.springboot.view.storeclasses;

import services.utils.StoreEnums;

import java.util.List;

public class StoreSummaryResponse {

    private StoreEnums storeSummaryQueryStatus;

    private List<StoreSummary> stores;


    public StoreEnums getStoreSummaryQueryStatus() {
        return storeSummaryQueryStatus;
    }

    public void setStoreSummaryQueryStatus(StoreEnums storeSummaryQueryStatus) {
        this.storeSummaryQueryStatus = storeSummaryQueryStatus;
    }

    public List<StoreSummary> getStores() {
        return stores;
    }

    public void setStores(List<StoreSummary> stores) {
        this.stores = stores;
    }

    @Override
    public String toString() {
        return "StoreSummaryResponse{" +
                "storeSummaryQueryStatus=" + storeSummaryQueryStatus +
                ", stores=" + stores +
                '}';
    }
}


