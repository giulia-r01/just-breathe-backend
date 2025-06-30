package it.epicode.just_breathe_backend.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
