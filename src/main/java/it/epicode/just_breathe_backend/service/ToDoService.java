package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.ToDoListDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;


    public ToDoList saveTask(ToDoListDto toDoListDto){
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ToDoList toDo = new ToDoList();
        toDo.setTitolo(toDoListDto.getTitolo());
        toDo.setDescrizione(toDoListDto.getDescrizione());
        LocalDateTime dataCreazione = OffsetDateTime.parse(toDoListDto.getDataCreazioneTask()).toLocalDateTime();
        toDo.setDataCreazioneTask(dataCreazione);
        toDo.setDataUltimaModificaTask(LocalDateTime.now());
        toDo.setTipoTask(toDoListDto.getTipoTask());
        toDo.setUtente(utenteAutenticato);

        return toDoRepository.save(toDo);
    }

    public ToDoList getToDo(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ToDoList toDo = toDoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appuntamento non trovato"));

        if (utenteAutenticato.getRuolo().name().equals("USER") &&
                !toDo.getUtente().getId().equals(utenteAutenticato.getId())) {
            throw new UnauthorizedException("Non puoi visualizzare/modificare/eliminare i task di un altro utente.");
        }

        return toDo;
    }

    public Page<ToDoList> getAllTasks(int page, int size, String sortBy) {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return toDoRepository.findByUtente(utenteAutenticato, pageable);
    }



    public ToDoList updateTask(Long id, ToDoListDto toDoListDto) throws NotFoundException {
        ToDoList toDo = getToDo(id);

        toDo.setTitolo(toDoListDto.getTitolo());
        toDo.setDescrizione(toDoListDto.getDescrizione());
        LocalDateTime dataCreazione = OffsetDateTime.parse(toDoListDto.getDataCreazioneTask()).toLocalDateTime();
        toDo.setDataCreazioneTask(dataCreazione);
        toDo.setTipoTask(toDoListDto.getTipoTask());
        toDo.setDataUltimaModificaTask(LocalDateTime.now());

        return toDoRepository.save(toDo);
    }



    public void deleteTask(Long id) throws NotFoundException {
        ToDoList toDo = getToDo(id);

        toDoRepository.delete(toDo);
    }


}
