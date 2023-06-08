package com.tradr.springboot.view.controllers;

import com.tradr.springboot.view.userclasses.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.registration.UserManagementService;
import services.utils.StaticMaps;
import services.utils.UserEnums;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserAuthorisationController {

    private final UserManagementService userManagementService;

    public UserAuthorisationController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @RequestMapping(value = "/user-login", method = RequestMethod.GET)
    public String userPageLoginView(Model model) {
        return "user-login";
    }

    @RequestMapping(value = "/user-login-verify", method = RequestMethod.POST)
    public String userPageLogin(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        model.addAttribute("email", email);
        model.addAttribute("password", password);

        return "login-success";
    }

    @PostMapping("auth")
    public ResponseEntity<UserEnums> authoriseCurrentUser(@RequestBody UserAuthKey userAuthKey) {
        boolean userIsAuthed = userManagementService.isAuthKeyValid(userAuthKey);
        return ResponseEntity.ok(userIsAuthed ? UserEnums.USER_AUTHORISED : UserEnums.USER_NOT_AUTHORISED);
    }

    @PostMapping("login")
    public ResponseEntity<PublicUserDetailsResponse> userLogin(@RequestBody UserLoginDetails userLoginDetails) {
        PublicUserDetailsResponse validatedPublicUserDetails = userManagementService.getUser(userLoginDetails);
        return ResponseEntity.ok(validatedPublicUserDetails);
    }

    @PostMapping("register")
    public ResponseEntity<UserRegistrationResponse> createNewUser(@RequestBody User user) {

        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
        userRegistrationResponse.setUserRegistrationQueryStatus(UserEnums.REGISTRATION_FAILED);
        user.setRegistrationDate(String.valueOf(LocalDate.now()));
        List<StaticMaps.RegistrationFailureEnums> failureReasons = new ArrayList<StaticMaps.RegistrationFailureEnums>();

        if (userManagementService.userExists(user.getEmail())) {
            userRegistrationResponse.setUserRegistrationQueryStatus(UserEnums.USER_EXISTS);
            return ResponseEntity.badRequest().body(userRegistrationResponse);
        }

        for (Field field : user.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(user);
                if (!field.getName().equals("authKey")
                        && !field.getName().equals("authKeyExpiry")
                        && !field.getName().equals("avatar")
                        && !field.getName().equals("uuid")
                        && !field.getName().equals("ownedStoreUUID")) {
                    if (fieldValue == null) {
                        failureReasons.add(StaticMaps.registrationFailures.get(field.getName()));
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (!failureReasons.isEmpty()) {
            userRegistrationResponse.setUserRegistrationQueryStatus(UserEnums.REGISTRATION_FAILED);
            userRegistrationResponse.setUserRegistrationFailureConditions(failureReasons);
            return ResponseEntity.badRequest().body(userRegistrationResponse);
        } else {
            boolean insertUser = userManagementService.insertUser(user);
            if (insertUser) {
                userRegistrationResponse.setUserRegistrationQueryStatus(UserEnums.REGISTRATION_SUCCESSFUL);
                return ResponseEntity.ok(userRegistrationResponse);
            } else {
                userRegistrationResponse.setUserRegistrationQueryStatus(UserEnums.REGISTRATION_FAILED);
                return ResponseEntity.badRequest().body(userRegistrationResponse);
            }
        }

    }
}
