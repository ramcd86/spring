package com.tradr.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreItemInsert {

  private String authKey;
  private String parentUUID;
  private StoreItem storeItem;
}
