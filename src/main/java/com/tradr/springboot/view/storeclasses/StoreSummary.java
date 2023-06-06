package com.tradr.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreSummary {
    private String storeTitle;
    private String storeDescription;
    private String storeTheme;
    private String ownUUID;
    private String publicStoreId;
}
