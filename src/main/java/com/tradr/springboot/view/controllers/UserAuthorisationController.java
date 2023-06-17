package com.tradr.springboot.view.controllers;

import com.tradr.springboot.view.userclasses.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.interfaces.CheckProfanity;
import services.registration.UserManagementService;
import services.resourceprocessor.ProfanityProcessorService;
import services.storemanagement.StoreManagementService;
import services.utils.StaticMaps;
import services.utils.UserEnums;

@Controller
public class UserAuthorisationController {

	private final UserManagementService userManagementService;
	private final StoreManagementService storeManagementService;

	public UserAuthorisationController(
		UserManagementService userManagementService,
		StoreManagementService storeManagementService
	) {
		this.userManagementService = userManagementService;
		this.storeManagementService = storeManagementService;
	}

	@RequestMapping(value = "/user-login", method = RequestMethod.GET)
	public String userPageLoginView(Model model) {
		return "user-login";
	}

	@RequestMapping(value = "/user-login-verify", method = RequestMethod.POST)
	public String userPageLogin(
		@RequestParam String email,
		@RequestParam String password,
		Model model
	) {
		model.addAttribute("email", email);
		model.addAttribute("password", password);

		return "login-success";
	}

	@PostMapping("auth")
	public ResponseEntity<UserEnums> authoriseCurrentUser(
		@RequestBody UserAuthKey userAuthKey
	) {
		boolean userIsAuthed = userManagementService.isAuthKeyValid(
			userAuthKey
		);
		return ResponseEntity.ok(
			userIsAuthed
				? UserEnums.USER_AUTHORISED
				: UserEnums.USER_NOT_AUTHORISED
		);
	}

	@PostMapping("login")
	public ResponseEntity<PublicUserDetailsResponse> userLogin(
		@RequestBody UserLoginDetails userLoginDetails
	) {
		PublicUserDetailsResponse validatedPublicUserDetails = userManagementService.getUser(
			userLoginDetails
		);
		return ResponseEntity.ok(validatedPublicUserDetails);
	}

	@PostMapping("register")
	public ResponseEntity<UserRegistrationResponse> createNewUser(
		@RequestBody User user
	) {
		// There's probably a lot here that could be refactored, and redone. This method has too many responsibilities for what it does.
		UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse();
		userRegistrationResponse.setUserRegistrationQueryStatus(
			UserEnums.REGISTRATION_FAILED
		);
		List<StaticMaps.RegistrationFailureEnums> failureReasons = new ArrayList<StaticMaps.RegistrationFailureEnums>();

		CheckProfanity check = (String stringToCheck) ->
			ProfanityProcessorService.inspectString(
				stringToCheck.toLowerCase(Locale.getDefault())
			);

		if (
			check.call(user.getEmail()) ||
			check.call(user.getUserName()) ||
			check.call(user.getFirstName()) ||
			check.call(user.getLastName())
		) {
			failureReasons.add(
				StaticMaps.registrationFailures.get("profanity")
			);

			userRegistrationResponse.setUserRegistrationFailureConditions(
				failureReasons
			);
			return ResponseEntity.badRequest().body(userRegistrationResponse);
		}

		user.setRegistrationDate(String.valueOf(LocalDate.now()));

		if (userManagementService.userExists(user.getEmail())) {
			userRegistrationResponse.setUserRegistrationQueryStatus(
				UserEnums.USER_EXISTS
			);
			return ResponseEntity.badRequest().body(userRegistrationResponse);
		}

		for (Field field : user.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(user);
				if (
					!field.getName().equals("authKey") &&
					!field.getName().equals("authKeyExpiry") &&
					!field.getName().equals("avatar") &&
					!field.getName().equals("uuid") &&
					!field.getName().equals("ownedStoreUUID")
				) {
					if (fieldValue == null) {
						failureReasons.add(
							StaticMaps.registrationFailures.get(field.getName())
						);
					}
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		if (!failureReasons.isEmpty()) {
			userRegistrationResponse.setUserRegistrationQueryStatus(
				UserEnums.REGISTRATION_FAILED
			);
			userRegistrationResponse.setUserRegistrationFailureConditions(
				failureReasons
			);

			return ResponseEntity.badRequest().body(userRegistrationResponse);
		} else {
			boolean insertUser = userManagementService.insertUser(user);
			if (insertUser) {
				userRegistrationResponse.setUserRegistrationQueryStatus(
					UserEnums.REGISTRATION_SUCCESSFUL
				);
				return ResponseEntity.ok(userRegistrationResponse);
			} else {
				userRegistrationResponse.setUserRegistrationQueryStatus(
					UserEnums.REGISTRATION_FAILED
				);
				return ResponseEntity
					.badRequest()
					.body(userRegistrationResponse);
			}
		}
	}

	@PostMapping("delete-user")
	public ResponseEntity<UserEnums> deleteExistingUser(
		@RequestBody UserAuthKey userAuthKey
	) {
		UserEnums deleteUserResponse = userManagementService.deleteUser(
			userAuthKey,
			storeManagementService
		);

		return ResponseEntity.status(HttpStatus.OK).body(deleteUserResponse);
	}

	@PostMapping("update-user")
	public ResponseEntity<UserEnums> updateUser(
		@RequestBody UserUpdate userUpate
	) {
		CheckProfanity check = (String stringToCheck) ->
			ProfanityProcessorService.inspectString(
				stringToCheck.toLowerCase(Locale.getDefault())
			);

		if (
			check.call(userUpate.getEmail()) ||
			check.call(userUpate.getUserName()) ||
			check.call(userUpate.getFirstName()) ||
			check.call(userUpate.getLastName())
		) {
			return ResponseEntity
				.badRequest()
				.body(UserEnums.USER_UPDATE_FAILED_PROFANITY);
		}

		UserEnums updateUserResponse = userManagementService.updateUser(
			userUpate
		);

		return ResponseEntity.status(HttpStatus.OK).body(updateUserResponse);
	}
}
