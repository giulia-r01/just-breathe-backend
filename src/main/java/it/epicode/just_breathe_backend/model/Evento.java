package it.epicode.just_breathe_backend.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Entity
public class Evento {
    @Id
    @GeneratedValue
    private Long id;

    private String nome;
    private String luogo;
    private LocalDateTime dataEvento;
    private String immagine;
    private String linkEsterno;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}
