package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RespiroDto {
    @NotEmpty(message = "Inserisci il nome dell'animazione (campo obbligatorio)")
    private String nome;
    private String descrizione;
    private int durata;
}
