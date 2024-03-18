package it.epicode.capstoneproject.requestDTO;

import jakarta.validation.constraints.NotBlank;

public record ComicDTO(
        @NotBlank(message = "Title field cannot be empty/null")
        String title,
        @NotBlank(message = "Author field cannot be empty/null")
        String author
) {
}
