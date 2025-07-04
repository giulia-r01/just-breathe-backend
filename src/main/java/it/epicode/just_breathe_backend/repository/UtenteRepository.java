package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long>,
        PagingAndSortingRepository<Utente, Long> {
    public Optional<Utente> findByUsername(String username);
    Optional<Utente> findByEmail(String email);
}
