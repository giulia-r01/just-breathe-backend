package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.ToDoListDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.ValidationException;
import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.repository.ToDoRepository;
import it.epicode.just_breathe_backend.service.ToDoService;
import it.epicode.just_breathe_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "tasks")
public class ToDoController {

    @Autowired
    ToDoRepository toDoRepository;

    @Autowired
    ToDoService toDoService;

    @Autowired
    UtenteService utenteService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoList saveTask(@RequestBody @Validated ToDoListDto toDoListDto,
                             BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return toDoService.saveTask(toDoListDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ToDoList getTask(@PathVariable Long id) throws NotFoundException {
        return toDoService.getToDo(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public Page<ToDoList> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCreazioneTask") String sortBy) {


        return toDoService.getAllTasks(page, size, sortBy);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ToDoList updateTask(@PathVariable Long id, @RequestBody
    @Validated ToDoListDto toDoListDto, BindingResult bindingResult)
            throws NotFoundException, ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return toDoService.updateTask(id, toDoListDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteTask(@PathVariable Long id) throws NotFoundException {
        toDoService.deleteTask(id);
    }
}
