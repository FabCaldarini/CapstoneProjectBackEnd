package it.epicode.capstoneproject.controllers;

import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.repositories.UserRepository;
import it.epicode.capstoneproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private UserService userService;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        System.out.println("Received user object: " + newUser.toString());
        System.out.println("Name: " + newUser.getName());
        System.out.println("Surname: " + newUser.getSurname());
        User savedUser = userRepository.save(newUser);

        if (savedUser.getId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        return userService.loginUser(user.getUsername(), user.getPassword());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/{id}")
    public void  deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

    }
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUserDetails(@PathVariable Long id, @RequestBody User updatedUserDetails) {
        User updatedUser = userService.updateUserDetails(id, updatedUserDetails);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
