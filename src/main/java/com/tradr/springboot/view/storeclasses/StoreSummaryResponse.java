package com.tradr.springboot.view.storeclasses;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import services.utils.StoreEnums;

@Data
@ToString
public class StoreSummaryResponse {

  private StoreEnums storeSummaryQueryStatus;
  private List<StoreSummary> stores;
}
