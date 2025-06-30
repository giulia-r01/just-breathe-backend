package it.epicode.just_breathe_backend.controller;

import it.epicode.backend.dto.UtenteDto;
import it.epicode.backend.exceptions.NotFoundException;
import it.epicode.backend.exceptions.UnauthorizedException;
import it.epicode.backend.exceptions.ValidationException;
import it.epicode.backend.model.Utente;
import it.epicode.backend.repository.UtenteRepository;
import it.epicode.backend.service.UtenteService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @PreAuthorize("hasAuthority('USER')")
    public Utente getUser(@PathVariable Long id) throws NotFoundException {
        return utenteService.getUser(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Utente> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id") String sortBy){
        return utenteService.getAllUsers(page, size, sortBy);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
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
    @PreAuthorize("hasAuthority('USER')")
    public Utente patchUser(@PathVariable Long id,
                            @RequestParam("file") MultipartFile file)
            throws NotFoundException, IOException {
        return utenteService.patchUser(id, file);
    }

    @PatchMapping("/{id}/username")
    @PreAuthorize("hasAuthority('USER')")
    public Utente patchUsername(@PathVariable Long id, @RequestParam String nuovoUsername) throws BadRequestException {
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

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Utente> patchPassword(
            @PathVariable Long id,
            @RequestParam String vecchiaPassword,
            @RequestParam String nuovaPassword) throws BadRequestException {

        Utente aggiornato = utenteService.patchPassword(id, vecchiaPassword, nuovaPassword);
        return ResponseEntity.ok(aggiornato);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteUser(@PathVariable Long id) throws NotFoundException {
        utenteService.deleteUser(id);
    }


}
