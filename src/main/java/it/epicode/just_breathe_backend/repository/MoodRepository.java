package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoodRepository extends JpaRepository<Mood, Long>,
        PagingAndSortingRepository<Mood, Long> {
    Page<Mood> findByUtente(Utente utente, Pageable pageable);
}
