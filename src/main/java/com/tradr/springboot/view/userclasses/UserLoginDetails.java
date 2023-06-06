package com.tradr.springboot.view.userclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginDetails {
    private String email;
    private String password;
}
