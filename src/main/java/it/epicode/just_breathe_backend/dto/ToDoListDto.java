package it.epicode.just_breathe_backend.dto;

import it.epicode.just_breathe_backend.enumeration.TipoTask;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ToDoListDto {
    @NotEmpty(message = "Inserisci il titolo del task per salvarlo in calendario (campo obbligatorio)")
    private String titolo;
    private String descrizione;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Inserisci lo stato del task (campo obbligatorio)")
    private TipoTask tipoTask;
    @NotEmpty(message = "Inserisci la data del task (campo obbligatorio)")
    private String dataCreazioneTask;
}
