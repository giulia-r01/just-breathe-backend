package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.LoginDto;
import it.epicode.just_breathe_backend.dto.RecuperoPswDto;
import it.epicode.just_breathe_backend.dto.ResetPswDto;
import it.epicode.just_breathe_backend.dto.UtenteDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.service.AuthService;
import it.epicode.just_breathe_backend.service.ResetTokenService;
import it.epicode.just_breathe_backend.service.UtenteService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResetTokenService resetTokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente register(@RequestBody @Validated UtenteDto utenteDto,
                           BindingResult bindingResult) throws ValidationException {

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return utenteService.saveUser(utenteDto);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Validated LoginDto loginDto,
                            BindingResult bindingResult) throws ValidationException, NotFoundException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }


        return authService.login(loginDto);
    }

    @PostMapping("/password/recupero")
    public ResponseEntity<String> inviaToken(@Valid @RequestBody RecuperoPswDto dto) {
        try {
            resetTokenService.inviaTokenRecuperoPassword(dto);
            return ResponseEntity.ok("Email di recupero password inviata con successo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resettaPassword(@Valid @RequestBody ResetPswDto dto) {
        try {
            resetTokenService.resettaPassword(dto);
            return ResponseEntity.ok("Password resettata con successo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
