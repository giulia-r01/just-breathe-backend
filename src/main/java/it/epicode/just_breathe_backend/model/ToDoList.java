package it.epicode.just_breathe_backend.model;

import it.epicode.just_breathe_backend.enumeration.TipoTask;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ToDoList {
    @Id
    @GeneratedValue
    private Long id;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataCreazioneTask;
    private LocalDateTime dataUltimaModificaTask;
    @Enumerated(EnumType.STRING)
    private TipoTask tipoTask;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

}
