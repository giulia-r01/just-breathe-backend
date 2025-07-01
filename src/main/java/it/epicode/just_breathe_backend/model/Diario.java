package it.epicode.just_breathe_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Diario {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime dataInserimento;
    private LocalDateTime dataUltimaModifica;
    @Column(columnDefinition = "TEXT")
    private String contenuto;
    private String titolo;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}
