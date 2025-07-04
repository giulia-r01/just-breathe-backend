package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UtenteDto {
    @NotEmpty(message = "Il campo 'nome' non può essere vuoto")
    private String nome;
    @NotEmpty(message = "Il campo 'cognome' non può essere vuoto")
    private String cognome;
    @NotEmpty(message = "Il campo 'email' non può essere vuoto")
    @Email(message = "L'email deve avere un formato valido, es: indirizzo@gmail.com")
    private String email;
    @NotEmpty(message = "Il campo 'username' non può essere vuoto")
    private String username;
    @NotEmpty(message = "Il campo 'password' non può essere vuoto")
    private String password;
    private LocalDateTime dataRegistrazione;
    private boolean attivo;
    private LocalDateTime lastAccess;
}
