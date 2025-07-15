package it.epicode.just_breathe_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTool {

    @Value("${jwt.duration}")
    private Long durata;
    @Value("${JWT_SECRET}")
    private String chiaveSegreta;

    @Autowired
    private UtenteService utenteService;

    public String createToken(Utente utente){

        return Jwts.builder().issuedAt(new Date()).
                expiration(new Date(System.currentTimeMillis()+durata))
                .subject(utente.getId()+"").signWith(Keys.
                        hmacShaKeyFor(chiaveSegreta.getBytes()))
                .compact();
    }

    public void validateToken(String token){
        Jwts.parser().verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .build().parse(token);
    }

    public Utente getUserFromToken(String token) throws NotFoundException {
        Long id = (long) Integer.parseInt(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(chiaveSegreta.getBytes()))
                .build().parseSignedClaims(token).getPayload().getSubject());

        return utenteService.getUser(id);
    }
}
