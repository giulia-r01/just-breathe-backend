package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranoRepository extends JpaRepository<Brano, Long> {
    List<Brano> findByMood(Mood mood);

}
