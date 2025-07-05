package it.epicode.just_breathe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.just_breathe_backend.enumeration.Ruolo;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
public class Utente implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    private String nome;
    private String cognome;
    @Column(unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;
    private String imgProfilo;
    private LocalDateTime dataRegistrazione;
    private boolean attivo;
    private LocalDateTime lastAccess;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Diario> diari = new ArrayList<>();

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ToDoList> toDoLists = new ArrayList<>();

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Mood> moods = new ArrayList<>();

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Evento> eventi = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ruolo.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return attivo;
    }
}
