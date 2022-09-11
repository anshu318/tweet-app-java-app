package com.tweetapp.service;

import com.tweetapp.config.constants.Constants;
import com.tweetapp.domain.UserModel;
import com.tweetapp.exception.PasswordMisMatchException;
import com.tweetapp.exception.UsernameAlreadyExists;
import com.tweetapp.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anshuman Panda
 * @project TweetApp-JAVA-API
 */
@Log4j2
@Component("user-model-service")
public class UserOperationsService {

    private final UserRepository userRepository;

    public UserOperationsService(@Qualifier("user-repository") final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // find user by username
    public UserModel findByUsername(final String username) {
        final UserModel userModel = userRepository.findByUsername(username);
        return new UserModel(userModel.getUsername(), userModel.getName()
                , userModel.getEmail(), "null", "null");
    }

    // create new user
    public UserModel createUser(final UserModel user) throws UsernameAlreadyExists {
        final UserModel foundedUser = userRepository.findByUsername(user.getUsername());
        if (foundedUser != null) {
            log.error(Constants.Username_Already_Exists + ": {}", user.getUsername());
            throw new UsernameAlreadyExists(Constants.Username_Already_Exists);
        }
        log.debug("registered user with userName: {}", user.getUsername());
        return userRepository.save(user);
    }

    // Method to return a list of all users
    public List<UserModel> getAllUsers() {
        final List<UserModel> userModels = userRepository.findAll();
        log.debug("total No of users: {}", userModels.size());
        return userModels.stream().map(user -> new UserModel(user.getUsername(), user.getName(), user.getEmail(), user.getContactNum(),
                "null")).collect(Collectors.toList());
    }

    // Method to change a user's password
    public UserModel changePassword(final String username, final String newPassword, final String contact) throws PasswordMisMatchException {
        final UserModel userDetails = userRepository.findByUsername(username);
        if (userDetails.getContactNum().equalsIgnoreCase(contact)
                && userDetails.getUsername().equalsIgnoreCase(username)) {
            userDetails.setPassword(newPassword);
            log.debug("password changed successfully for user: {}", username);
            return userRepository.save(userDetails);
        } else {
            log.error(String.format("unable to change password for user: %s", username));
            throw new PasswordMisMatchException(String.format(Constants.Password_MisMatch_Exception + " : %s", username));
        }
    }

    // Method to search for like users by username
    public List<UserModel> getUsersByUsername(final String username) {
        return userRepository.searchByUsername(username);
    }
}
