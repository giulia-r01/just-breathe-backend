package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.UtenteDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.ValidationException;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.UtenteRepository;
import it.epicode.just_breathe_backend.service.UtenteService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    UtenteRepository utenteRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Utente getUser(@PathVariable Long id) throws NotFoundException {
        return utenteService.getUser(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Utente getProfilo() throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return utenteService.getUser(utenteAutenticato.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Utente updateUser(@PathVariable Long id, @RequestBody
                               @Validated UtenteDto utenteDto,
                               BindingResult bindingResult) throws NotFoundException, ValidationException {

        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return utenteService.updateUser(id, utenteDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Utente patchUser(@PathVariable Long id,
                            @RequestParam("file") MultipartFile file)
            throws NotFoundException, IOException {
        return utenteService.patchUser(id, file);
    }

    @PatchMapping("/{id}/username")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Utente patchUsername(@PathVariable Long id, @RequestParam String nuovoUsername) throws NotFoundException, BadRequestException {
        return utenteService.patchUsername(id, nuovoUsername);
    }


    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Utente> patchPassword(
            @PathVariable Long id,
            @RequestParam String vecchiaPassword,
            @RequestParam String nuovaPassword) throws BadRequestException {

        Utente aggiornato = utenteService.patchPassword(id, vecchiaPassword, nuovaPassword);
        return ResponseEntity.ok(aggiornato);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public void deleteUser(@PathVariable Long id) throws NotFoundException {
        utenteService.deleteUser(id);
    }


}
