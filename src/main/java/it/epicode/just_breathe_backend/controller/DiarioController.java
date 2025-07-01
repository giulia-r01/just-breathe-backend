package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.DiarioDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.ValidationException;
import it.epicode.just_breathe_backend.model.Diario;
import it.epicode.just_breathe_backend.repository.DiarioRepository;
import it.epicode.just_breathe_backend.service.DiarioService;
import it.epicode.just_breathe_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/diari")
public class DiarioController {

    @Autowired
    private DiarioRepository diarioRepository;

    @Autowired
    private DiarioService diarioService;

    @Autowired
    private UtenteService utenteService;


    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Diario saveDiario(@RequestBody @Validated DiarioDto diarioDto,
                             BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return diarioService.saveDiario(diarioDto);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Diario getDiario(@PathVariable Long id) throws NotFoundException {
        return diarioService.getDiario(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Diario> getAllDiari(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "id") String sortBy){
        return diarioService.getAllDiarioUtente(page, size, sortBy);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Diario updateDiario(@PathVariable Long id, @RequestBody
    @Validated DiarioDto diarioDto, BindingResult bindingResult)
            throws NotFoundException, ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return diarioService.updateDiario(id, diarioDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteDiario(@PathVariable Long id) throws NotFoundException {
        diarioService.deleteDiario(id);
    }

}
