package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.BackOfficeDto;
import it.epicode.just_breathe_backend.enumeration.Ruolo;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.service.BackOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/backoffice")
public class BackOfficeController {


    @Autowired
    BackOfficeService backOfficeService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<BackOfficeDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        return backOfficeService.getAllUsers(page, size, sortBy);
    }

    @PutMapping("/{id}/ruolo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Utente changeUserRole(
            @PathVariable Long id,
            @RequestParam Ruolo nuovoRuolo) throws NotFoundException, UnauthorizedException {

        return backOfficeService.changeUserRole(id, nuovoRuolo);
    }


    @PutMapping("/{id}/attivo")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Utente setUserActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean attivo) throws NotFoundException {

        return backOfficeService.setUserActiveStatus(id,attivo);
    }


    @GetMapping("/{id}/ultimo-accesso")
    @PreAuthorize("hasAuthority('ADMIN')")
    public LocalDateTime getLastAccess(@PathVariable Long id) throws NotFoundException {
        return backOfficeService.getLastAccess(id);
    }


    @GetMapping("/mood-statistiche")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Long> getMoodStatistics() {
        return backOfficeService.getMoodStatistics();
    }

    @GetMapping("/statistiche/media-attivita-utente")
    @PreAuthorize("hasAuthority('ADMIN')")
    public double getMediaAttivitaPerUtente() {
        return backOfficeService.getAverageActivitiesPerUser();
    }

    @GetMapping("/statistiche/attivita-utenti")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Map<String, Object>> getAttivitaDettagliatePerUtente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return backOfficeService.getAttivitaDettagliatePerUtente(pageable);
    }


}
