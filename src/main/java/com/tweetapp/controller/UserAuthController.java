package com.tweetapp.controller;

import com.tweetapp.config.constants.Constants;
import com.tweetapp.dto.AuthenticationRequest;
import com.tweetapp.dto.AuthenticationResponse;
import com.tweetapp.domain.UserModel;
import com.tweetapp.exception.UsernameAlreadyExists;
import com.tweetapp.service.UserOperationsService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */

@RestController
@Api
@Log4j2
@CrossOrigin(origins = "*", allowedHeaders="*")
@RequestMapping("/tweets")
public class UserAuthController {

	private final UserOperationsService userModelService;

	private final AuthenticationManager authenticationManager;

	public UserAuthController(@Qualifier("user-model-service") UserOperationsService userModelService,
			AuthenticationManager authenticationManager) {
		this.userModelService = userModelService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping(value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
			MediaType.ALL_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
					MediaType.ALL_VALUE })
	public ResponseEntity<?> subscribeClient(@RequestBody UserModel userModel) {

		try {
			UserModel savedUser = userModelService.createUser(userModel);
			return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
		} catch (UsernameAlreadyExists e) {
			return new ResponseEntity<>(new AuthenticationResponse(Constants.Invalid_Username_Exception),
					HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/login", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
			MediaType.ALL_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE,
					MediaType.ALL_VALUE })
	public ResponseEntity<?> authenticateClient(@RequestBody AuthenticationRequest authenticationRequest) {
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			log.debug("successfully logged in with userName: {}", username);
		} catch (Exception e) {
			return new ResponseEntity<>(new AuthenticationResponse("Bad Credentials for" + username),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(userModelService.findByUsername(username), HttpStatus.OK);
	}
}
