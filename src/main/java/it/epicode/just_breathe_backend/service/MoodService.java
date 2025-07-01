package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.BranoDto;
import it.epicode.just_breathe_backend.dto.MoodDto;
import it.epicode.just_breathe_backend.enumeration.TipoMood;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.BranoRepository;
import it.epicode.just_breathe_backend.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MoodService {

    @Autowired
    MoodRepository moodRepository;

    @Autowired
    BranoRepository branoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    public Mood saveMood(TipoMood tipoMood, Utente utente) {
        Mood mood = new Mood();
        mood.setTipoMood(tipoMood);
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return moodRepository.findByUtente(utenteAutenticato, pageable);
    }

    public Mood updateMood(Long id, MoodDto moodDto) throws NotFoundException {
        Mood mood = getMood(id);

        mood.setTipoMood(moodDto.getTipoMood());

        return moodRepository.save(mood);
    }

    public Brano addBranoToMood(Long moodId, BranoDto branoDto) throws NotFoundException {
        Mood mood = getMood(moodId);

        String query = branoDto.getTitoloBrano().replace(" ", "+");
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + query + "&key=" + youtubeApiKey;

        String linkEsterno = null;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("items")) {
                Object itemsObj = response.get("items");
                if (itemsObj instanceof java.util.List<?> items && !items.isEmpty()) {
                    Map<String, Object> firstItem = (Map<String, Object>) items.get(0);
                    if (firstItem.containsKey("id")) {
                        Map<String, Object> idMap = (Map<String, Object>) firstItem.get("id");
                        if (idMap != null && idMap.containsKey("videoId")) {
                            String videoId = idMap.get("videoId").toString();
                            linkEsterno = "https://www.youtube.com/watch?v=" + videoId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Errore chiamata API esterna: " + e.getMessage());
        }

        Brano brano = new Brano();
        brano.setTitoloBrano(branoDto.getTitoloBrano());
        brano.setLink(linkEsterno != null ? linkEsterno : branoDto.getLink());
        brano.setMood(mood);

        return branoRepository.save(brano);
    }

    public List<Brano> getBraniByMood(Long moodId) throws NotFoundException {
        Mood mood = getMood(moodId);
        return branoRepository.findByMood(mood);
    }

    public Brano getBranoById(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Brano brano = branoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brano non trovato con id " + id));
        if (utenteAutenticato.getRuolo().name().equals("USER") && !brano.getMood().getUtente().getId().equals(utenteAutenticato.getId())) {
            throw new UnauthorizedException("Non puoi visualizzare il brano di un altro utente.");
        }
        return brano;
    }

    public Page<Brano> getAllBrani(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return branoRepository.findAll(pageable);
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
