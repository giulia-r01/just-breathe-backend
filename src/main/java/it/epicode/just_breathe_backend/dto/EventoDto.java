package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventoDto {
    @NotEmpty(message = "Inserisci il nome dell'evento (campo obbligatorio)")
    private String nome;
    private String luogo;
    private LocalDateTime dataEvento;
    private String immagine;
    private String linkEsterno;
}
