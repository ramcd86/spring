package com.tradr.springboot.view.userclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PublicUserDetails {

  private String userName;
  private String firstName;
  private String lastName;
  private String email;
  private String dob;
  private String registrationDate;
  private String userAvatar;
  private String authKey;
  private String uuid;
}
