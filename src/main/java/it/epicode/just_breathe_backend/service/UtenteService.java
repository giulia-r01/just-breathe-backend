package it.epicode.just_breathe_backend.service;

import com.cloudinary.Cloudinary;
import it.epicode.just_breathe_backend.dto.UtenteDto;
import it.epicode.just_breathe_backend.enumeration.Ruolo;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Cloudinary cloudinary;

    public Utente saveUser(UtenteDto utenteDto){
        Utente utente = new Utente();
        utente.setNome(utenteDto.getNome());
        utente.setCognome(utenteDto.getCognome());
        utente.setEmail(utenteDto.getEmail());
        utente.setUsername(utenteDto.getUsername());
        utente.setDataRegistrazione(LocalDateTime.now());
        utente.setAttivo(utente.isAttivo());
        utente.setLastAccess(LocalDateTime.now());
        utente.setPassword(passwordEncoder.encode(utenteDto.getPassword()));
        utente.setRuolo(Ruolo.USER);

        sendMail(utente.getEmail(), utente);

        return utenteRepository.save(utente);
    }

    public Utente getUser(Long id) throws NotFoundException {

        return utenteRepository.findById(id).
                orElseThrow(()-> new NotFoundException("Non è stato trovato alcun utente con id " + id));
    }



    public Utente updateUser(Long id, UtenteDto utenteDto) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (utenteAutenticato.getRuolo() == Ruolo.USER && !utenteAutenticato.getId().equals(id)) {
            throw new UnauthorizedException("Non puoi modificare un altro utente.");

    }

        Utente utenteDaAggiornare = getUser(id);

        utenteDaAggiornare.setNome(utenteDto.getNome());
        utenteDaAggiornare.setCognome(utenteDto.getCognome());
        utenteDaAggiornare.setEmail(utenteDto.getEmail());
        utenteDaAggiornare.setUsername(utenteDto.getUsername());
        if (!passwordEncoder.matches(utenteDto.getPassword(), utenteDaAggiornare.getPassword())){
            utenteDaAggiornare.setPassword(passwordEncoder.encode(utenteDto.getPassword()));
        }


        return utenteRepository.save(utenteDaAggiornare);
    }

    public Utente patchUser(Long id, MultipartFile file) throws NotFoundException, IOException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Utente userDaPatchare = getUser(id);
        String url = (String) cloudinary.uploader().upload(file.getBytes(),
                Collections.emptyMap()).get("url");

        if (utenteAutenticato.getRuolo().name().equals("USER") && !utenteAutenticato.getId().equals(id)) {
            throw new UnauthorizedException("Puoi cambiare solo la tua immagine del profilo.");
        }

        userDaPatchare.setImgProfilo(url);
        return utenteRepository.save(userDaPatchare);
    }

    public Utente patchUsername(Long id, String nuovoUsername) throws NotFoundException, BadRequestException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!utenteAutenticato.getId().equals(id)) {
            throw new UnauthorizedException("Puoi modificare solo il tuo username.");
        }

        if (utenteRepository.findByUsername(nuovoUsername).isPresent()) {
            throw new BadRequestException("Questo username è già stato utilizzato.");
        }

        if (utenteAutenticato.getUsername().equals(nuovoUsername)) {
            throw new BadRequestException("Il nuovo username deve essere diverso dal precedente.");
        }

        utenteAutenticato.setUsername(nuovoUsername);
        return utenteRepository.save(utenteAutenticato);
    }

    public Utente patchPassword(Long id, String vecchiaPassword, String nuovaPassword) throws BadRequestException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!utenteAutenticato.getId().equals(id)) {
            throw new UnauthorizedException("Non puoi modificare la password di altri utenti.");
        }

        if (!passwordEncoder.matches(vecchiaPassword, utenteAutenticato.getPassword())) {
            throw new UnauthorizedException("La vecchia password non è corretta.");
        }

        if (passwordEncoder.matches(nuovaPassword, utenteAutenticato.getPassword())) {
            throw new BadRequestException("La nuova password deve essere diversa dalla precedente.");
        }

        utenteAutenticato.setPassword(passwordEncoder.encode(nuovaPassword));
        return utenteRepository.save(utenteAutenticato);
    }


    private void sendMail(String email, Utente utente) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Just Breathe - Registrazione utente");
        message.setText("☀\uFE0F Benvenut* in Just Breathe, " + utente.getNome() + "!\n\n" +
                        "La tua registrazione è avvenuta con successo! 🎉\n\n" +
                        "Ora puoi cominciare a rilassarti e a organizzare al meglio la tua giornata. 😊\n\n" +
                        "Comincia ora: 3, 2, 1... Just Breathe! \uD83C\uDF3F");

        javaMailSender.send(message);
    }


    public void deleteUser(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (utenteAutenticato.getRuolo().name().equals("USER") && utenteAutenticato.getId() != id) {
            throw new UnauthorizedException("Non puoi eliminare un altro utente.");
        }

        Utente utenteDaEliminare = getUser(id);

        utenteRepository.delete(utenteDaEliminare);
    }
}
