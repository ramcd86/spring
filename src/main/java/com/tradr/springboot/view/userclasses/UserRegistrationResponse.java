package com.tradr.springboot.view.userclasses;

import lombok.Data;
import lombok.ToString;
import services.utils.StaticMaps;
import services.utils.UserEnums;

import java.util.List;

@Data
@ToString
public class UserRegistrationResponse {
    private UserEnums userRegistrationQueryStatus;
    private List<StaticMaps.RegistrationFailureEnums> userRegistrationFailureConditions;
}
