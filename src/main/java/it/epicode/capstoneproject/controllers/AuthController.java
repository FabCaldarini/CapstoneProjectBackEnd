package it.epicode.capstoneproject.controllers;


import io.jsonwebtoken.security.Password;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import it.epicode.capstoneproject.requestDTO.UserLoginDTO;
import it.epicode.capstoneproject.requestDTO.UserRegistrationDTO;
import it.epicode.capstoneproject.responseDTO.AccessTokenRes;
import it.epicode.capstoneproject.security.JwtTools;
import it.epicode.capstoneproject.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTools jwtTools;
    @Autowired
    private PasswordEncoder encoder;

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Validated UserRegistrationDTO userRegistrationDTO) {
        try {
            authService.register(userRegistrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
        } catch (Exception e) {
            logger.severe("Registration failed: " + e.getMessage()); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated  UserLoginDTO loginDTO) {
        try {
            AccessTokenRes accessToken = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

            accessToken.setUsername(loginDTO.getUsername());

            return ResponseEntity.ok().body(accessToken);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

}



