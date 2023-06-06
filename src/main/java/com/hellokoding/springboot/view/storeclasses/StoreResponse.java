package com.hellokoding.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;
import services.utils.StoreEnums;

@Data
@ToString
public class StoreResponse {
    private StoreEnums storeQueryResponseStatus;
    private Store store;
}
