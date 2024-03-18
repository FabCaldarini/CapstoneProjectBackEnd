package it.epicode.capstoneproject.requestDTO;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
public record CommentDTO(

        String text


) {
    public String getText() {
        return text;
    }


}

