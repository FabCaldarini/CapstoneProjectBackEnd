package it.epicode.capstoneproject.entities;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {
    @NotBlank(message = "name is mandatory")
    private String nome;
    @NotBlank(message = "surname is mandatory")
    private String surname;
    @NotBlank(message = "username is mandatory")
    private String username;
    @NotBlank(message = "password is mandatory")
    private String password;
}

