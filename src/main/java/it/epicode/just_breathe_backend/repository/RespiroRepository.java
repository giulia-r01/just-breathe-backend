package it.epicode.just_breathe_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.epicode.just_breathe_backend.model.Respiro;

import java.util.Optional;

public interface RespiroRepository extends JpaRepository<Respiro, Long> {
    Optional<Respiro> findTopByOrderByDataCreazioneDesc();
}
