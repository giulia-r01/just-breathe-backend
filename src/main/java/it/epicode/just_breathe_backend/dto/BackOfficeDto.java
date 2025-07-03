package it.epicode.just_breathe_backend.dto;

import it.epicode.just_breathe_backend.enumeration.Ruolo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BackOfficeDto {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String username;
    private Ruolo ruolo;
    private LocalDateTime dataRegistrazione;
    private boolean attivo;
    private LocalDateTime lastAccess;
}
