package it.epicode.capstoneproject.services;

import it.epicode.capstoneproject.entities.Role;
import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.entities.UserRequest;
import it.epicode.capstoneproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Long findUserIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User registerUser(User newUser) {
        try {
            newUser.setRole(Role.USER);
            User registeredUser = userRepository.save(newUser);
            System.out.println("User succesfully registered: " + registeredUser);
            return registeredUser;
        } catch (Exception e) {
            System.out.println("Error while trying to register User: " + newUser);
            e.printStackTrace();
            return null;
        }
    }
    public ResponseEntity<User> loginUser(String username, String password) {
        User loggedInUser = userRepository.findByUsernameAndPassword(username, password);
        if (loggedInUser != null) {
            return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (EmptyResultDataAccessException e) {
            System.err.println("User not found: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    public User updateUserDetails(Long id, User updatedUserDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(updatedUserDetails.getName());
            existingUser.setSurname(updatedUserDetails.getSurname());
            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }



    public User createUser(User user) {
        return userRepository.save(user);
    }
@Autowired
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }


}
