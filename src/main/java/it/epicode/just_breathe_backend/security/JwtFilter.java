package it.epicode.just_breathe_backend.security;

import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Utente;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTool jwtTool;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException
                    ("Token non presente, non sei autorizzato ad usare il servizio richiesto");
        } else {

            String token = authorization.substring(7);


            jwtTool.validateToken(token);


            try {
                Utente utente = jwtTool.getUserFromToken(token);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(utente,
                                null, utente.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (NotFoundException e) {
                throw new UnauthorizedException("Utente collegato al token non trovato");
            }

            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();

        return matcher.match("/auth/**", path) ||
                matcher.match("/password/recupero", path) ||
                matcher.match("/password/reset", path);
    }
}
