package com.tradr.springboot.view;

import com.tradr.springboot.view.userclasses.PublicUserDetails;
import com.tradr.springboot.view.userclasses.User;
import com.tradr.springboot.view.userclasses.UserAuthKey;
import com.tradr.springboot.view.userclasses.UserLoginDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.registration.UserManagementService;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public ResponseEntity<Boolean> authoriseCurrentUser(@RequestBody UserAuthKey userAuthKey)
            throws Exception {
        CompletableFuture<Boolean> userIsAuthed = CompletableFuture.supplyAsync(() -> {
            try {
                return userManagementService.isAuthKeyValid(userAuthKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return ResponseEntity.ok(userIsAuthed.join());
    }

    @PostMapping("login")
    public ResponseEntity<PublicUserDetails> userLogin(@RequestBody UserLoginDetails userLoginDetails)
            throws Exception {

        CompletableFuture<PublicUserDetails> publicUserDetailsCompletableFuture = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return userManagementService.getUser(userLoginDetails);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        PublicUserDetails validatedPublicUserDetails = publicUserDetailsCompletableFuture.join();
        return ResponseEntity.ok(validatedPublicUserDetails);
    }

    @PostMapping("register")
    public ResponseEntity<Object> createNewUser(@RequestBody User user) {

        CompletableFuture<Boolean> userExistsJoin = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return userManagementService.userExists(user.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        if (userExistsJoin.join()) {
            return ResponseEntity.badRequest().body("User exists");
        }

        user.setRegistrationDate(String.valueOf(LocalDate.now()));

        List<String> failureReasons = new ArrayList<String>();

        for (Field field : user.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(user);
                if (!field.getName().equals("authKey") && !field.getName().equals("authKeyExpiry")
                        && !field.getName().equals("avatar")) {
                    if (fieldValue == null) {
                        failureReasons.add("Value of " + field.getName() + " is empty");
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (failureReasons.isEmpty()) {
            CompletableFuture<Boolean> userInsertedFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return userManagementService.insertUser(user);
                } catch (SQLException | NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            if (userInsertedFuture.join()) {
                return ResponseEntity.ok("User inserted.");
            }
        }

        return ResponseEntity.badRequest().body(failureReasons);
    }
}
