package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.BackOfficeDto;
import it.epicode.just_breathe_backend.enumeration.Ruolo;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.MoodRepository;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackOfficeService {

    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    MoodRepository moodRepository;

    public Page<BackOfficeDto> getAllUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Utente> utentiPage = utenteRepository.findAll(pageable);

        return utentiPage.map(utente -> {
            BackOfficeDto backOfficeDto = new BackOfficeDto();
            backOfficeDto.setId(utente.getId());
            backOfficeDto.setNome(utente.getNome());
            backOfficeDto.setCognome(utente.getCognome());
            backOfficeDto.setEmail(utente.getEmail());
            backOfficeDto.setUsername(utente.getUsername());
            backOfficeDto.setRuolo(utente.getRuolo());
            backOfficeDto.setDataRegistrazione(utente.getDataRegistrazione());
            backOfficeDto.setAttivo(utente.isAttivo());
            backOfficeDto.setLastAccess(utente.getLastAccess());
            return backOfficeDto;
        });
    }

    public Utente changeUserRole(Long id, Ruolo nuovoRuolo) throws NotFoundException, UnauthorizedException {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));

        Utente adminAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (utente.getId().equals(adminAutenticato.getId()) && nuovoRuolo == Ruolo.USER) {
            throw new UnauthorizedException("Non puoi rimuovere i tuoi permessi di admin.");
        }

        utente.setRuolo(nuovoRuolo);
        return utenteRepository.save(utente);
    }

    public Utente setUserActiveStatus(Long id, boolean attivo) throws NotFoundException {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        utente.setAttivo(attivo);
        return utenteRepository.save(utente);
    }

    public LocalDateTime getLastAccess(Long id) throws NotFoundException {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        return utente.getLastAccess();
    }

    public Map<String, Long> getMoodStatistics() {
        List<Object[]> results = moodRepository.countMoodGroupedByTipoMood();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            String tipoMood = row[0].toString();
            Long count = (Long) row[1];
            stats.put(tipoMood, count);
        }
        return stats;
    }


    public double getAverageActivitiesPerUser() {
        List<Utente> utenti = utenteRepository.findAll();

        if (utenti.isEmpty()) return 0;

        long totalActivities = 0;

        for (Utente u : utenti) {
            int diariCount = u.getDiari() != null ? u.getDiari().size() : 0;
            int todoCount = u.getToDoLists() != null ? u.getToDoLists().size() : 0;
            int moodCount = u.getMoods() != null ? u.getMoods().size() : 0;

            totalActivities += (diariCount + todoCount + moodCount);
        }

        return (double) totalActivities / utenti.size();
    }
}

