package it.epicode.just_breathe_backend.dto;

import it.epicode.just_breathe_backend.enumeration.TipoMood;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MoodDto {
    @NotEmpty(message = "Inserisci il titolo del brano (campo obbligatorio)")
    private String titoloBrano;
    private String link;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Come ti senti? Digita un mood - NOSTALGICO, ARRABBIATO, STRESSATO, RILASSATO, ENERGICO, FELICE, ANSIOSO, ANNOIATO, SOPRAFFATTO (campo obbligatorio)")
    private TipoMood tipoMood;
}
