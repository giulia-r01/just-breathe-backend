package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private ToDoRepository toDoRepository;

    @GetMapping(value = "/ics", produces = "text/calendar")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public String exportCalendar() {

        Utente utente = (Utente) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        List<ToDoList> tasks =
                toDoRepository.findByUtenteOrderByDataCreazioneTaskAsc(utente);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//Just Breathe//Calendar//IT\n");

        for (ToDoList task : tasks) {
            ics.append("BEGIN:VEVENT\n");
            ics.append("UID:task-").append(task.getId()).append("@justbreathe\n");
            ics.append("DTSTART;VALUE=DATE:")
                    .append(task.getDataCreazioneTask().toLocalDate().format(formatter))
                    .append("\n");
            ics.append("SUMMARY:")
                    .append(task.getTitolo())
                    .append("\n");

            if (task.getDescrizione() != null && !task.getDescrizione().isBlank()) {
                ics.append("DESCRIPTION:")
                        .append(task.getDescrizione())
                        .append("\n");
            }

            ics.append("END:VEVENT\n");
        }

        ics.append("END:VCALENDAR");
        return ics.toString();
    }
}
