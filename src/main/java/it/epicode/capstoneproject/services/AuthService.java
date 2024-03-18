package it.epicode.capstoneproject.services;


import it.epicode.capstoneproject.entities.User;
import it.epicode.capstoneproject.exceptions.BadRequestException;
import it.epicode.capstoneproject.exceptions.InternalServerErrorException;
import it.epicode.capstoneproject.exceptions.UnauthorizedException;
import it.epicode.capstoneproject.repositories.UserRepository;
import it.epicode.capstoneproject.requestDTO.UserRegistrationDTO;
import it.epicode.capstoneproject.responseDTO.AccessTokenRes;
import it.epicode.capstoneproject.security.JwtTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtTools jwtTools;


    public User register(UserRegistrationDTO userRegDTO) throws BadRequestException, InternalServerErrorException {
        User u = new User(userRegDTO.getUsername(), userRegDTO.getEmail(), encoder.encode(userRegDTO.getPassword()),
                userRegDTO.getName(), userRegDTO.getSurname(),userRegDTO.getDateOfBirth());
        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException e) {
            if (userRepository.getAllEmails().contains(u.getEmail()))
                throw new BadRequestException("email already exists, impossible to create");
            if (userRepository.getAllUsernames().contains(u.getUsername()))
                throw new BadRequestException("username already exists, impossible to create username");
            throw new InternalServerErrorException("Data integrity violation error: " + e.getMessage());

        }
    }

    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }



    public AccessTokenRes login(String username, String password) throws UnauthorizedException {
        User u = userRepository.findByUsername(username).orElseThrow(
                () -> new UnauthorizedException("Email or password wrong")
        );

        if (!encoder.matches(password, u.getPassword())) {
            throw new UnauthorizedException("Email or password wrong");
        }

        String exp = "3600000";

        // Create AccessTokenRes with token and username
        return new AccessTokenRes(jwtTools.createToken(u, exp), username);
    }



}
