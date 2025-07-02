package it.epicode.just_breathe_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.epicode.just_breathe_backend.model.Respiro;

public interface RespiroRepository extends JpaRepository<Respiro, Long> {
}
