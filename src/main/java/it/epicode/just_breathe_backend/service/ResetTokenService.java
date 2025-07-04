package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.RecuperoPswDto;
import it.epicode.just_breathe_backend.dto.ResetPswDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.TokenInvalidException;
import it.epicode.just_breathe_backend.model.ResetToken;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.ResetTokenRepository;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {
    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private final int TOKEN_EXPIRATION_MINUTES = 30;

    public void inviaTokenRecuperoPassword(RecuperoPswDto dto) throws NotFoundException {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(dto.getEmail());
        if (utenteOpt.isEmpty()) {
            throw new NotFoundException("Utente non trovato con questa email");
        }
        Utente utente = utenteOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setUsed(false);
        resetToken.setUtente(utente);

        resetTokenRepository.save(resetToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(utente.getEmail());
        message.setSubject("Recupero password - Just Breathe");
        String linkReset = frontendBaseUrl + "/reset-password?token=" + token;
        message.setText("Ciao "+ utente.getNome() +
                " ,\n\n Hai richiesto il reset della password per il tuo account Just Breathe.\n\n"
                + "Per completare la procedura, clicca sul link qui sotto (valido per 30 minuti):\n"
                + linkReset + "\n\n"
                + "Se non hai richiesto questo reset, ignora pure questa email — la tua password resterà invariata.\n\n"
                + "Grazie,\n"
                + "Il team di Just Breathe");
        mailSender.send(message);
    }

    public void resettaPassword(ResetPswDto dto) throws TokenInvalidException {
        Optional<ResetToken> tokenOpt = resetTokenRepository.findByToken(dto.getToken());
        if (tokenOpt.isEmpty()) {
            throw new TokenInvalidException("Token non valido");
        }

        ResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed()) {
            throw new TokenInvalidException("Token già utilizzato");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidException("Token scaduto");
        }

        Utente utente = resetToken.getUtente();
        utente.setPassword(passwordEncoder.encode(dto.getNuovaPassword()));

        utenteRepository.save(utente);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }
}
