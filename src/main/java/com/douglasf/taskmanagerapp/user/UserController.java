package com.douglasf.taskmanagerapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping("/")
  public ResponseEntity createUser(@RequestBody UserModel userData) {

    /**
     * To avoid duplicated users, first check if user exists in the database and
     * return a 400 error if it does. User names should be unique.
     */
    var userQueryData = this.userRepository.findByUserName(userData.getUserName());

    if (userQueryData != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This user already exists.");
    }

    /** Hash the password, create the user, and return a success response. */
    var passwordHash = BCrypt.withDefaults().hashToString(12, userData.getPassword().toCharArray());

    userData.setPassword(passwordHash);
    var savedUser = this.userRepository.save(userData);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
  }
}
