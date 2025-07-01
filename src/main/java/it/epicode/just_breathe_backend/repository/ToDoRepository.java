package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;

public interface ToDoRepository extends JpaRepository<ToDoList, Long>,
        PagingAndSortingRepository<ToDoList, Long>{

    Page<ToDoList> findByUtente(Utente utente, Pageable pageable);

}
