package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPswDto {
    @NotEmpty(message = "Inserisci il token (campo obbligatorio)")
    private String token;

    @NotEmpty(message = "Inserisci la nuova password (campo obbligatorio)")
    private String nuovaPassword;
}
