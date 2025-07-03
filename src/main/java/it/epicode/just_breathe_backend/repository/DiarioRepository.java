package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.Diario;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiarioRepository extends JpaRepository<Diario, Long>{

    Page<Diario> findByUtente(Utente utente, Pageable pageable);

    Optional<Diario> findTopByUtenteIdOrderByDataUltimaModificaDesc(Long utenteId);
}
