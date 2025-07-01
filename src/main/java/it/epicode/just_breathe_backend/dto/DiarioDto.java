package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DiarioDto {
    @NotEmpty(message = "Il titolo non può essere vuoto")
    private String titolo;

    @NotEmpty(message = "Il contenuto non può essere vuoto")
    private String contenuto;
}
