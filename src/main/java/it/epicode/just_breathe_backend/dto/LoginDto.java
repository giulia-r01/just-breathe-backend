package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {
    @NotEmpty(message = "Inserisci il tuo username per proseguire (campo obbligatorio)")
    private String username;
    @NotEmpty(message = "Inserisci la tua password (campo obbligatorio)")
    private String password;
}
