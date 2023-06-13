package com.tradr.springboot.view.userclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {

  private String userName;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String dob;
  private String authKey;
  private String authKeyExpiry;
  private String avatar;
  private String registrationDate;
}
