package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.ResetToken;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
    Optional<ResetToken> findByUtente(Utente utente);
}
