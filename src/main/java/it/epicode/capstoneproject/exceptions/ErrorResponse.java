package it.epicode.capstoneproject.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private int statusCode;
    private String error;
    private String message;
    private Timestamp timestamp;

    public ErrorResponse(HttpStatus httpStatus, String error, String message){
        statusCode = httpStatus.value();
        timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.error = error;
        this.message = message;
    }

}
