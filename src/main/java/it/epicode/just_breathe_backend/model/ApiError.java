package it.epicode.just_breathe_backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private String message;
    private LocalDateTime dataErrore;
}
