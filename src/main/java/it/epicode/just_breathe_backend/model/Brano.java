package it.epicode.just_breathe_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Brano {
    @Id
    @GeneratedValue
    private Long id;

    private String titoloBrano;
    private String link;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;
}
