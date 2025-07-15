package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.BranoDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.BranoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BranoService {

    @Autowired
    BranoRepository branoRepository;

    @Autowired
    MoodService moodService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${YOUTUBE_API_KEY}")
    private String youtubeApiKey;

    public Brano addBranoToMood(Long moodId, BranoDto branoDto) throws NotFoundException {
        Mood mood = moodService.getMood(moodId);

        String query = branoDto.getTitoloBrano().replace(" ", "+");
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + query + "&key=" + youtubeApiKey;

        String linkEsterno = null;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("items")) {
                Object itemsObj = response.get("items");
                if (itemsObj instanceof List<?> items && !items.isEmpty()) {
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
        Mood mood = moodService.getMood(moodId);
        return branoRepository.findByMood(mood);
    }

    public Brano getBranoById(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Brano brano = branoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brano non trovato con id " + id));
        if (utenteAutenticato.getRuolo().name().equals("USER") &&
                !brano.getMood().getUtente().getId().equals(utenteAutenticato.getId())) {
            throw new UnauthorizedException("Non puoi visualizzare/modificare/eliminare il brano di un altro utente.");
        }
        return brano;
    }

    public Page<Brano> getAllBrani(int page, int size, String sortBy) {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return branoRepository.findByMood_Utente(utenteAutenticato, pageable);
    }

    public Brano updateBrano(Long id, BranoDto branoDto) throws NotFoundException {
        Brano brano = getBranoById(id);

        brano.setTitoloBrano(branoDto.getTitoloBrano());

        if (branoDto.getLink() != null && !branoDto.getLink().isBlank()) {
            brano.setLink(branoDto.getLink());
        } else {
            String query = branoDto.getTitoloBrano().replace(" ", "+");
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + query + "&key=" + youtubeApiKey;

            try {
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response != null && response.containsKey("items")) {
                    Object itemsObj = response.get("items");
                    if (itemsObj instanceof List<?> items && !items.isEmpty()) {
                        Map<String, Object> firstItem = (Map<String, Object>) items.get(0);
                        if (firstItem.containsKey("id")) {
                            Map<String, Object> idMap = (Map<String, Object>) firstItem.get("id");
                            if (idMap != null && idMap.containsKey("videoId")) {
                                String videoId = idMap.get("videoId").toString();
                                String nuovoLink = "https://www.youtube.com/watch?v=" + videoId;
                                brano.setLink(nuovoLink);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore chiamata API esterna: " + e.getMessage());
            }
        }

        return branoRepository.save(brano);
    }

    public Brano cambiaMoodDelBrano(Long branoId, Long nuovoMoodId) throws NotFoundException {
        Brano brano = getBranoById(branoId);
        Mood nuovoMood = moodService.getMood(nuovoMoodId);

        brano.setMood(nuovoMood);
        return branoRepository.save(brano);
    }

    public void deleteBrano(Long id) throws NotFoundException {
        Brano brano = getBranoById(id);
        branoRepository.delete(brano);
    }
}
