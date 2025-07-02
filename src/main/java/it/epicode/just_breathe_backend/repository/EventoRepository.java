package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.Evento;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventoRepository extends JpaRepository<Evento, Long> {

    Page<Evento> findByUtente(Utente utente, Pageable pageable);
}
