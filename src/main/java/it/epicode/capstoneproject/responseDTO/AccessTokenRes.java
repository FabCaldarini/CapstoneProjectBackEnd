package it.epicode.capstoneproject.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class AccessTokenRes {
    private String token;
    // Constructor, getters, and setters
    @Setter
    private String username; // Add this field

}
