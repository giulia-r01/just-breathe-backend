package it.epicode.just_breathe_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Diario {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDate dataInserimento;
    private LocalDate dataUltimaModifica;
    @Column(columnDefinition = "TEXT")
    private String contenuto;
    private String titolo;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}
