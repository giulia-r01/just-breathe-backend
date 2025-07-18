package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.MoodDto;
import it.epicode.just_breathe_backend.enumeration.TipoMood;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MoodService {

    @Autowired
    MoodRepository moodRepository;


    public Mood saveMood(TipoMood tipoMood, Utente utente) {
        Mood mood = new Mood();
        mood.setTipoMood(tipoMood);
        mood.setDataCreazione(LocalDateTime.now());
        mood.setUtente(utente);
        return moodRepository.save(mood);
    }



    public Mood getMood(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Mood mood = moodRepository.findById(id).orElseThrow(()->
                new NotFoundException("Nessun mood presente con id " + id));

        if (utenteAutenticato.getRuolo().name().equals("USER") &&
                !mood.getUtente().getId().equals(utenteAutenticato.getId())) {
            throw new UnauthorizedException("Non puoi visualizzare/modificare/eliminare il mood di un altro utente.");
        }
        return mood;
    }


    public Page<Mood> getAllMoodsByUser(int page, int size, String sortBy) {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return moodRepository.findByUtente(utenteAutenticato, pageable);
    }


    public Mood getUltimoMoodPerUtente(Long utenteId) {
        return moodRepository.findTopByUtenteIdOrderByDataCreazioneDesc(utenteId)
                .orElse(null);
    }

    public Mood updateMood(Long id, MoodDto moodDto) throws NotFoundException {
        Mood mood = getMood(id);

        mood.setTipoMood(moodDto.getTipoMood());

        return moodRepository.save(mood);
    }


    public Mood patchTipoMood(Long id, TipoMood tipoMood) throws NotFoundException {
        Mood mood = getMood(id);
        mood.setTipoMood(tipoMood);
        return moodRepository.save(mood);
    }

    public void deleteMood(Long id) throws NotFoundException {
        Mood mood = getMood(id);
        moodRepository.delete(mood);
    }
}
