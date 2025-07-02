package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.RespiroDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.ValidationException;
import it.epicode.just_breathe_backend.model.Respiro;
import it.epicode.just_breathe_backend.service.RespiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "respirazioni")
public class RespiroController {

    @Autowired
    private RespiroService respiroService;

    @GetMapping
    public List<Respiro> getAllRespiri() {
        return respiroService.getAllRespiri();
    }

    @GetMapping("/{id}")
    public Respiro getRespiro(@PathVariable Long id) throws NotFoundException {
        return respiroService.getRespiro(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Respiro saveRespiro(@RequestBody @Validated RespiroDto respiroDto,
                               BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }

        return respiroService.saveRespiro(respiroDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Respiro updateRespiro(@PathVariable Long id, @RequestBody
    @Validated RespiroDto respiroDto, BindingResult bindingResult)
            throws NotFoundException, ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return respiroService.updateRespiro(id, respiroDto);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteRespiro(@PathVariable Long id) throws NotFoundException {
        respiroService.deleteRespiro(id);
    }
}
