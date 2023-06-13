package com.tradr.springboot.view.storeclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreItemDeletion {

  private String authKey;
  private String storeItemPublicId;
}
