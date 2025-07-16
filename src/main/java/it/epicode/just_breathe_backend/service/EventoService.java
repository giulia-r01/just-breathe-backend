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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${TICKETMASTER_API_KEY}")
    private String ticketmasterApiKey;

    private static final Map<String, String> traduzioneCitta = Map.ofEntries(
            Map.entry("roma", "Roma"),
            Map.entry("milano", "Milano"),
            Map.entry("napoli", "Napoli"),
            Map.entry("torino", "Torino"),
            Map.entry("firenze", "Firenze"),
            Map.entry("bologna", "Bologna"),
            Map.entry("genova", "Genova"),
            Map.entry("palermo", "Palermo"),
            Map.entry("verona", "Verona"),
            Map.entry("cagliari", "Cagliari"),
            Map.entry("venezia", "Venezia"),
            Map.entry("trieste", "Trieste"),
            Map.entry("padova", "Padova"),
            Map.entry("perugia", "Perugia"),
            Map.entry("trento", "Trento"),
            Map.entry("modena", "Modena"),
            Map.entry("reggio emilia", "Reggio Emilia"),
            Map.entry("rimini", "Rimini"),
            Map.entry("taranto", "Taranto"),
            Map.entry("ancona", "Ancona"),
            Map.entry("bari", "Bari")
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
        String cittaTradotta = traduzioneCitta.getOrDefault(cittaLower, citta);

        String cittaEncoded = URLEncoder.encode(cittaTradotta, StandardCharsets.UTF_8);
        String url = "https://app.ticketmaster.com/discovery/v2/events.json"
                + "?city=" + cittaEncoded
                + "&countryCode=IT"
                + "&locale=it"
                + "&apikey=" + ticketmasterApiKey;


        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> embedded = (Map<String, Object>) body.get("_embedded");

                if (embedded == null || !embedded.containsKey("events")) {
                    EventoDto dtoVuoto = new EventoDto();
                    dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
                    eventiEsterni.add(dtoVuoto);
                    return eventiEsterni;
                }

                List<Map<String, Object>> events = (List<Map<String, Object>>) embedded.get("events");

                for (Map<String, Object> event : events) {
                    EventoDto dto = new EventoDto();

                    // Nome evento
                    dto.setNome((String) event.get("name"));

                    // Data evento
                    Map<String, Object> dates = (Map<String, Object>) event.get("dates");
                    if (dates != null && dates.get("start") instanceof Map) {
                        String dateStr = (String) ((Map<String, Object>) dates.get("start")).get("dateTime");
                        if (dateStr != null) {
                            try {
                                dto.setDataEvento(ZonedDateTime.parse(dateStr).toLocalDateTime());
                            } catch (Exception e) {
                                dto.setDataEvento(null);
                            }
                        }
                    }

                    // Escludi eventi passati
                    if (dto.getDataEvento() != null && dto.getDataEvento().isBefore(java.time.LocalDateTime.now())) {
                        continue;
                    }

                    // Luogo
                    Map<String, Object> venue = null;
                    try {
                        venue = (Map<String, Object>) ((List<?>) ((Map<?, ?>) event.get("_embedded")).get("venues")).get(0);
                    } catch (Exception ignored) {}
                    if (venue != null && venue.get("name") instanceof String) {
                        dto.setLuogo((String) venue.get("name"));
                    }

                    // Immagine
                    List<Map<String, Object>> images = (List<Map<String, Object>>) event.get("images");
                    if (images != null && !images.isEmpty()) {
                        dto.setImmagine((String) images.get(0).get("url"));
                    }

                    // Link esterno
                    dto.setLinkEsterno((String) event.get("url"));

                    eventiEsterni.add(dto);
                }
            }
        } catch (Exception e) {
            EventoDto dtoErrore = new EventoDto();
            dtoErrore.setNome("Errore durante il recupero degli eventi: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            eventiEsterni.add(dtoErrore);
        }

        if (eventiEsterni.isEmpty()) {
            EventoDto dtoVuoto = new EventoDto();
            dtoVuoto.setNome("Non ci sono eventi disponibili nella città cercata.");
            eventiEsterni.add(dtoVuoto);
        }

        eventiEsterni.sort(Comparator.comparing(EventoDto::getDataEvento, Comparator.nullsLast(Comparator.naturalOrder())));
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

    public Evento getEventoById(Long id) throws NotFoundException, UnauthorizedException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento con id " + id + " non trovato"));

        if (utenteAutenticato.getRuolo().name().equals("USER") &&
                !evento.getUtente().getId().equals(utenteAutenticato.getId())) {
            throw new UnauthorizedException("Non puoi visualizzare l'evento di un altro utente.");
        }

        return evento;
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
