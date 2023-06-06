package com.tradr.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class StoreItemInsert {
    private String parentUUID;
    private List<StoreItem> storeItems;
}
