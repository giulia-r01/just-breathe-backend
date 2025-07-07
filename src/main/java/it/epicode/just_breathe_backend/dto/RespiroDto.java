package it.epicode.just_breathe_backend.dto;

import it.epicode.just_breathe_backend.enumeration.TipoRespiro;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RespiroDto {
    @NotEmpty(message = "Inserisci il nome dell'animazione (campo obbligatorio)")
    private String nome;
    private String descrizione;
    private int inspiraSecondi;
    private int trattieniSecondi;
    private int espiraSecondi;
    private LocalDateTime dataCreazione;
    @NotNull(message = "Seleziona una categoria")
    private TipoRespiro categoria;
}
