package com.tradr.springboot.view.userclasses;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import services.utils.StaticMaps;
import services.utils.UserEnums;

@Data
@ToString
public class UserRegistrationResponse {

  private UserEnums userRegistrationQueryStatus;
  private List<StaticMaps.RegistrationFailureEnums> userRegistrationFailureConditions;
}
