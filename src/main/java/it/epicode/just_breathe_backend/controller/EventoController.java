package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.EventoDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Evento;
import it.epicode.just_breathe_backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // cerco eventi esterni per città
    @GetMapping("/esterni")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<EventoDto> cercaEventiEsterni(@RequestParam(required = false) String citta) {
        return eventoService.getAllEventi(citta);
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Evento salvaEvento(@RequestBody EventoDto eventoDto) {
        return eventoService.saveEvento(eventoDto);
    }

    @GetMapping("/utente")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<Evento> getEventiUtente(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "dataEvento") String sortBy) {
        return eventoService.getAllEventiByUser(page, size, sortBy);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Evento getEventoById(@PathVariable Long id) throws NotFoundException, UnauthorizedException {
        return eventoService.getEventoById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public void eliminaEvento(@PathVariable Long id) throws Exception {
        eventoService.deleteEvento(id);
    }
}
