package it.epicode.just_breathe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.just_breathe_backend.enumeration.TipoMood;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Mood {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoMood tipoMood;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Brano> brani;
}
