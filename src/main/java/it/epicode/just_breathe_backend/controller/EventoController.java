package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.EventoDto;
import it.epicode.just_breathe_backend.model.Evento;
import it.epicode.just_breathe_backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // Endpoint per cercare eventi esterni per città
    @GetMapping("/esterni")
    public List<EventoDto> cercaEventiEsterni(@RequestParam(required = false) String citta) {
        return eventoService.getAllEventi(citta);
    }

    // Altri endpoint (salvataggio, lista eventi utente, cancellazione)...

    @PostMapping
    public Evento salvaEvento(@RequestBody EventoDto eventoDto) {
        return eventoService.saveEvento(eventoDto);
    }

    @GetMapping("/utente")
    public Page<Evento> getEventiUtente(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "dataEvento") String sortBy) {
        return eventoService.getAllEventiByUser(page, size, sortBy);
    }

    @DeleteMapping("/{id}")
    public void eliminaEvento(@PathVariable Long id) throws Exception {
        eventoService.deleteEvento(id);
    }
}
