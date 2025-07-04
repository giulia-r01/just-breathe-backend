package it.epicode.just_breathe_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ResetToken {
    @Id
    @GeneratedValue
    private Long id;

    private String token;

    private LocalDateTime expiryDate;

    private boolean used = false;

    @OneToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
}
