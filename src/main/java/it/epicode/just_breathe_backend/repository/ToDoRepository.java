package it.epicode.just_breathe_backend.repository;

import it.epicode.just_breathe_backend.enumeration.TipoTask;
import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDoList, Long>
        {



    List<ToDoList> findByUtenteOrderByDataCreazioneTaskAsc(Utente utente);

    List<ToDoList> findByUtenteIdAndTipoTaskNotOrderByDataCreazioneTaskAsc(Long utenteId, TipoTask tipoTask);

}
