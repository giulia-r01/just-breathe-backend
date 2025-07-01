package it.epicode.just_breathe_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BranoDto {
    @NotEmpty(message = "Inserisci il titolo del brano (campo obbligatorio)")
    private String titoloBrano;

    private String link;
}
