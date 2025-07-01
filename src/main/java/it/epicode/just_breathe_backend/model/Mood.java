package it.epicode.just_breathe_backend.model;

import it.epicode.just_breathe_backend.enumeration.TipoMood;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Mood {
    @Id
    @GeneratedValue
    private Long id;
    private String titoloBrano;
    private String link;
    private LocalDate dataCreazione;
    @Enumerated(EnumType.STRING)
    private TipoMood tipoMood;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}
