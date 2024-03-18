package it.epicode.capstoneproject.requests;

import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
    @Getter
    @Setter
    private String username;
    private String password;

}
