package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.LoginDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import it.epicode.just_breathe_backend.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, String> login(LoginDto loginDto) throws NotFoundException {
        Utente utente = utenteRepository.findByUsername(loginDto.getUsername()).orElseThrow(
                ()->new NotFoundException("Utente con username/password non trovato"));

        if (passwordEncoder.matches(loginDto.getPassword(), utente.getPassword())){

            utente.setLastAccess(LocalDateTime.now());
            utenteRepository.save(utente);

            String token = jwtTool.createToken(utente);
            String ruolo = utente.getRuolo().name();

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("ruolo", ruolo);
            return response;
        }else {
            throw new NotFoundException("Utente con username/password non trovato");
        }


    }
}
