package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.EventoDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Evento;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${predicthq.api.token}")
    private String predicthqApiToken;

    // Mappa statica città → coordinate (lat, lon)
    private static final Map<String, String> cityCoordinates = Map.of(
            "roma", "41.9028,12.4964",
            "milano", "45.4642,9.1900",
            "napoli", "40.8518,14.2681",
            "torino", "45.0703,7.6869",
            "firenze", "43.7696,11.2558",
            "bologna", "44.4949,11.3426"
            // aggiungi altre città se vuoi
    );

    public List<EventoDto> getAllEventi(String citta) {
        List<EventoDto> eventiEsterni = new ArrayList<>();

        if (citta == null || citta.isBlank()) {
            EventoDto dtoVuoto = new EventoDto();
            dtoVuoto.setNome("Devi inserire una città valida.");
            eventiEsterni.add(dtoVuoto);
            return eventiEsterni;
        }

        String cittaLower = citta.toLowerCase();

        if (!cityCoordinates.containsKey(cittaLower)) {
            EventoDto dtoVuoto = new EventoDto();
            dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
            eventiEsterni.add(dtoVuoto);
            return eventiEsterni;
        }

        String latLon = cityCoordinates.get(cittaLower);
        String[] parts = latLon.split(",");
        String lat = parts[0].trim();
        String lon = parts[1].trim();

        String now = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);

        String url = "https://api.predicthq.com/v1/events/?active.gte=" + now +
                "&location_around.origin=" + lat + "," + lon +
                "&location_around.radius=50km" +
                "&limit=20";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + predicthqApiToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                Object resultsObj = body.get("results");

                if (!(resultsObj instanceof List)) {
                    EventoDto dtoVuoto = new EventoDto();
                    dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
                    eventiEsterni.add(dtoVuoto);
                    return eventiEsterni;
                }

                List<?> rawResults = (List<?>) resultsObj;

                if (rawResults.isEmpty()) {
                    EventoDto dtoVuoto = new EventoDto();
                    dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
                    eventiEsterni.add(dtoVuoto);
                    return eventiEsterni;
                }

                for (Object eventObj : rawResults) {
                    if (!(eventObj instanceof Map)) continue;

                    Map<String, Object> event = (Map<String, Object>) eventObj;
                    EventoDto dto = new EventoDto();

                    dto.setNome((String) event.get("title"));

                    // Gestione robusta campo "start"
                    String startDate = null;
                    Object startObj = event.get("start");
                    if (startObj instanceof Map) {
                        Map<String, Object> startMap = (Map<String, Object>) startObj;
                        Object dateObj = startMap.get("date");
                        if (dateObj instanceof String) {
                            startDate = (String) dateObj;
                        }
                    } else if (startObj instanceof String) {
                        startDate = (String) startObj;
                    }

                    if (startDate != null) {
                        try {
                            dto.setDataEvento(ZonedDateTime.parse(startDate).toLocalDateTime());
                        } catch (Exception e) {
                            // fallback lascio null
                        }
                    }

                    // Filtra eventi passati
                    if (dto.getDataEvento() != null && dto.getDataEvento().isBefore(java.time.LocalDateTime.now())) {
                        continue; // skip evento passato
                    }

                    // Gestione entities per luogo
                    Object entitiesObj = event.get("entities");
                    if (entitiesObj instanceof List) {
                        List<?> entities = (List<?>) entitiesObj;
                        for (Object entObj : entities) {
                            if (entObj instanceof Map) {
                                Map<String, Object> ent = (Map<String, Object>) entObj;
                                if ("venue".equals(ent.get("type"))) {
                                    if (ent.containsKey("formatted_address")) {
                                        dto.setLuogo((String) ent.get("formatted_address"));
                                    } else if (ent.containsKey("name")) {
                                        dto.setLuogo((String) ent.get("name"));
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    dto.setImmagine(null);
                    dto.setLinkEsterno(null);

                    eventiEsterni.add(dto);
                }

                // Se dopo il filtro la lista è vuota, aggiungi messaggio amichevole
                if (eventiEsterni.isEmpty()) {
                    EventoDto dtoVuoto = new EventoDto();
                    dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
                    eventiEsterni.add(dtoVuoto);
                }
            }
        } catch (Exception e) {
            EventoDto dtoErrore = new EventoDto();
            dtoErrore.setNome("Errore durante il recupero degli eventi: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            eventiEsterni.add(dtoErrore);
        }

        return eventiEsterni;
    }




    public Evento saveEvento(EventoDto eventoDto) {
        Utente utente = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Evento evento = new Evento();
        evento.setNome(eventoDto.getNome());
        evento.setLuogo(eventoDto.getLuogo());
        evento.setDataEvento(eventoDto.getDataEvento());
        evento.setImmagine(eventoDto.getImmagine());
        evento.setLinkEsterno(eventoDto.getLinkEsterno());
        evento.setUtente(utente);

        return eventoRepository.save(evento);
    }

    public Page<Evento> getAllEventiByUser(int page, int size, String sortBy) {
        Utente utente = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return eventoRepository.findByUtente(utente, pageable);
    }

    public void deleteEvento(Long id) throws NotFoundException {
        Utente utente = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nessun evento trovato con id " + id));
        if (!evento.getUtente().getId().equals(utente.getId())) {
            throw new UnauthorizedException("Non puoi eliminare l'evento di un altro utente.");
        }
        eventoRepository.delete(evento);
    }
}
