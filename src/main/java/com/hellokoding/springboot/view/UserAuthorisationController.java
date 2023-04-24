package com.hellokoding.springboot.view;

import com.hellokoding.springboot.view.userclasses.PublicUserDetails;
import com.hellokoding.springboot.view.userclasses.User;
import com.hellokoding.springboot.view.userclasses.UserAuthKey;
import com.hellokoding.springboot.view.userclasses.UserLoginDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping({"/", "/register"})
    public String register(Model model,
                           @RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        return "Registration";
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
