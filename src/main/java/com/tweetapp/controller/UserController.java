package com.tweetapp.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tweetapp.dto.*;
import com.tweetapp.service.*;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@Log4j2
@RequestMapping("/tweets")
@CrossOrigin(origins = "*", allowedHeaders="*")
@RestController
public class UserController {

	private final UserOperationsService userModelService;

	public UserController(@Qualifier("user-model-service") UserOperationsService userModelService) {
		this.userModelService = userModelService;
	}

	// method to update user password
	@PutMapping(value = "/{username}/forgot", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
			MediaType.ALL_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
					MediaType.ALL_VALUE })
	public ResponseEntity<?> changePassword(@PathVariable("username") String username,
			@RequestBody NewPassword newPassword) {
		try {
			log.debug("changing password for user: {}", username);
			return new ResponseEntity<>(
					userModelService.changePassword(username, newPassword.getNewPassword(), newPassword.getContact()),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new AuthenticationResponse("Unable to change password"),
					HttpStatus.BAD_REQUEST);
		}
	}

	// Method to retrieve all users list
	@GetMapping(value = "/users/all", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
			MediaType.ALL_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
					MediaType.ALL_VALUE })
	public ResponseEntity<?> getAllUsers() {
		log.debug("fetching total users");
		return new ResponseEntity<>(userModelService.getAllUsers(), HttpStatus.OK);

	}

	// method to search for like users by username
	@GetMapping(value = "/user/search/{username}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE })
	public ResponseEntity<?> searchForUsers(@PathVariable String username) {
		log.debug("fetching user by userName: {}", username);
		return new ResponseEntity<>(userModelService.getUsersByUsername(username), HttpStatus.OK);
	}
}
