package it.epicode.capstoneproject.responseDTO;

public class CustomErrorResponse {

    private String error;
    private String message;

    public CustomErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
