package it.epicode.just_breathe_backend.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
