package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RecuperoPswDto {
    @NotEmpty(message = "Il campo 'email' non può essere vuoto")
    @Email(message = "Inserisci un'email valida (es: email@gmail.com)")
    private String email;
}
