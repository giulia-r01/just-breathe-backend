package it.epicode.just_breathe_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Respiro {
    @Id
    @GeneratedValue
    private Long id;
    private String nome;
    private String descrizione;
    private int durata;
    private LocalDateTime dataCreazione;
}
