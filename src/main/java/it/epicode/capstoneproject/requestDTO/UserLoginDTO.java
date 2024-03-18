package it.epicode.capstoneproject.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public record UserLoginDTO(
        @NotBlank(message = "Username is obligatory and cannot be an empty field")
        String username,
        @NotBlank(message = "Password is obligatory and cannot be an empty field")
        String password


) {


        public String getUsername() {
                return username;
        }
        public String getPassword(){
                return password;
        }
}




