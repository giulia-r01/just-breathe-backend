package it.epicode.just_breathe_backend.model;

import it.epicode.just_breathe_backend.enumeration.TipoRespiro;
import jakarta.persistence.*;
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
    private int inspiraSecondi;
    private int trattieniSecondi;
    private int espiraSecondi;
    private LocalDateTime dataCreazione;
    @Enumerated(EnumType.STRING)
    private TipoRespiro categoria;
}
