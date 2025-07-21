package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.BackOfficeDto;
import it.epicode.just_breathe_backend.enumeration.Ruolo;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.MoodRepository;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackOfficeService {

    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    MoodRepository moodRepository;

    @Autowired
    private JavaMailSender javaMailSender;

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

        if (!attivo) {
            inviaEmailDisattivazione(utente);
        }

        if (attivo) {
            inviaEmailRiattivazione(utente);
        }

        return utenteRepository.save(utente);
    }

    public void inviaEmailDisattivazione(Utente utente) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utente.getEmail());
        message.setSubject("Just Breathe - Account disattivato");

        message.setText("Ciao " + utente.getNome() + "!\n\n" +
                "Il tuo account è stato disattivato, probabilmente a causa di inattività o per motivi amministrativi.\n\n" +
                "Se pensi che si tratti di un errore o vuoi riattivare il tuo profilo, contatta il supporto all’indirizzo: just.breathe.tam@gmail.com\n\n" +
                "Grazie per aver fatto parte della nostra community 💙\n\n" +
                "A presto dal team di Just Breathe 🌿");

        javaMailSender.send(message);
    }

    private void inviaEmailRiattivazione(Utente utente) {
        String email = utente.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Just Breathe - Account riattivato");
        message.setText("Ciao " + utente.getNome() + "!\n\n" +
                "Il tuo account su Just Breathe è stato riattivato con successo.\n\n" +
                "Ora puoi accedere di nuovo alla tua area personale.\n\n" +
                "Buona continuazione dal team di Just Breathe 🌿");

        javaMailSender.send(message);
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
            int eventiCount = u.getEventi() != null ? u.getEventi().size() : 0;

            totalActivities += (diariCount + todoCount + moodCount + eventiCount);
        }

        return (double) totalActivities / utenti.size();
    }

    public Page<Map<String, Object>> getAttivitaDettagliatePerUtente(Pageable pageable) {
        Page<Utente> utentiPage = utenteRepository.findAll(pageable);

        List<Map<String, Object>> contenuto = utentiPage.stream().map(u -> {
            Map<String, Object> mappa = new HashMap<>();
            mappa.put("id", u.getId());
            mappa.put("username", u.getUsername());
            mappa.put("diari", u.getDiari() != null ? u.getDiari().size() : 0);
            mappa.put("tasks", u.getToDoLists() != null ? u.getToDoLists().size() : 0);
            mappa.put("eventi", u.getEventi() != null ? u.getEventi().size() : 0);
            mappa.put("moods", u.getMoods() != null ? u.getMoods().size() : 0);
            return mappa;
        }).toList();

        return new PageImpl<>(contenuto, pageable, utentiPage.getTotalElements());
    }

}

