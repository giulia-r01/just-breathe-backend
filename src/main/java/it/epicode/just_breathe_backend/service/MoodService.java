package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.MoodDto;
import it.epicode.just_breathe_backend.enumeration.TipoMood;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
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
import java.util.Map;

@Service
public class MoodService {

    @Autowired
    MoodRepository moodRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

//    public Mood saveMood(MoodDto moodDto){
//        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        Mood mood = new Mood();
//        mood.setDataCreazione(LocalDate.now());
//        mood.setTitoloBrano(moodDto.getTitoloBrano());
//        mood.setTipoMood(moodDto.getTipoMood());
//        mood.setLink(moodDto.getLink());
//        mood.setUtente(utenteAutenticato);
//
//        return moodRepository.save(mood);
//    }

    public Mood saveMood(MoodDto moodDto) {
        // 1. Prendo l'utente autenticato dal contesto di sicurezza (Spring Security)
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Preparo la query per cercare su YouTube il titolo del brano (sostituisco spazi con +)
        String query = moodDto.getTitoloBrano().replace(" ", "+");

        // 3. Costruisco l'URL per la chiamata all'API YouTube Data v3, con la query e la tua API Key (da sostituire)
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + query + "&key=" + youtubeApiKey;

        String linkEsterno = null;

        try {
            // 4. Chiamata HTTP GET all'API YouTube, mappando la risposta JSON in una Map generica
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 5. Se la risposta non è nulla e contiene la chiave "items" (lista di risultati)
            if (response != null && response.containsKey("items")) {
                Object itemsObj = response.get("items");

                // 6. Controllo che "items" sia una lista non vuota
                if (itemsObj instanceof java.util.List<?> items && !items.isEmpty()) {
                    Map<String, Object> firstItem = (Map<String, Object>) items.get(0);

                    // 7. Controllo che il primo elemento abbia la chiave "id"
                    if (firstItem.containsKey("id")) {
                        Map<String, Object> idMap = (Map<String, Object>) firstItem.get("id");

                        // 8. Controllo che dentro "id" ci sia "videoId" (identificativo video YouTube)
                        if (idMap != null && idMap.containsKey("videoId")) {
                            String videoId = idMap.get("videoId").toString();

                            // 9. Costruisco il link diretto al video YouTube
                            linkEsterno = "https://www.youtube.com/watch?v=" + videoId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 10. In caso di errore nella chiamata, stampo un messaggio ma continuo senza bloccare
            System.out.println("Errore chiamata API esterna: " + e.getMessage());
        }

        // 11. Creo un nuovo oggetto Mood da salvare
        Mood mood = new Mood();
        mood.setDataCreazione(LocalDate.now());
        mood.setTitoloBrano(moodDto.getTitoloBrano());

        // 12. Se ho un link esterno valido, lo uso, altrimenti uso quello che arriva dal client
        mood.setLink(linkEsterno != null ? linkEsterno : moodDto.getLink());

        mood.setTipoMood(moodDto.getTipoMood());
        mood.setUtente(utenteAutenticato);

        // 13. Salvo il mood nel database e lo ritorno
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

        mood.setTitoloBrano(moodDto.getTitoloBrano());
        mood.setLink(moodDto.getLink());
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
