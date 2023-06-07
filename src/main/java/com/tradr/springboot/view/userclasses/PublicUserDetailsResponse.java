package com.tradr.springboot.view.userclasses;

import lombok.Data;
import lombok.ToString;
import services.utils.UserEnums;

@Data
@ToString
public class PublicUserDetailsResponse {
    private UserEnums publicUserQueryResponseStatus;
    private PublicUserDetails publicUserDetails;
}
