package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.BranoDto;
import it.epicode.just_breathe_backend.dto.MoodDto;
import it.epicode.just_breathe_backend.enumeration.TipoMood;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.exceptions.ValidationException;
import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.model.Mood;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.BranoRepository;
import it.epicode.just_breathe_backend.service.MoodService;
import it.epicode.just_breathe_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "moods")
public class MoodController {


    @Autowired
    MoodService moodService;

    @Autowired
    UtenteService utenteService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public Mood createMood(@RequestParam TipoMood tipoMood) {
        Utente utente = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return moodService.saveMood(tipoMood, utente);
    }

    @PostMapping("/{moodId}/brani")
    @PreAuthorize("hasAuthority('USER')")
    public Brano addBrano(@PathVariable Long moodId, @RequestBody @Validated BranoDto branoDto) throws NotFoundException {
        return moodService.addBranoToMood(moodId, branoDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Mood getMood(@PathVariable Long id) throws NotFoundException {
        return moodService.getMood(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public Page<Mood> getMoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {


        return moodService.getAllMoodsByUser(page, size, sortBy);
    }

    @GetMapping("/{moodId}/brani")
    @PreAuthorize("hasAuthority('USER')")
    public List<Brano> getBrani(@PathVariable Long moodId) throws NotFoundException {
        return moodService.getBraniByMood(moodId);
    }

    @GetMapping("/brani/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Brano getBranoById(@PathVariable Long id) throws NotFoundException {
        return moodService.getBranoById(id);
    }

    @GetMapping("/brani")
    @PreAuthorize("hasAuthority('USER')")
    public Page<Brano> getAllBrani(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return moodService.getAllBrani(page, size, sortBy);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Mood updateMood(@PathVariable Long id, @RequestBody
    @Validated MoodDto moodDto, BindingResult bindingResult)
            throws NotFoundException, ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().
                    stream().map(objectError -> objectError.getDefaultMessage())
                    .reduce("",(e,s)->e+s));
        }
        return moodService.updateMood(id, moodDto);
    }

    @PatchMapping("/{id}/mood")
    @PreAuthorize("hasAuthority('USER')")
    public Mood patchTipoMood(@PathVariable Long id, @RequestParam TipoMood tipoMood)
            throws NotFoundException {
        return moodService.patchTipoMood(id, tipoMood);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteMood(@PathVariable Long id) throws NotFoundException {
        moodService.deleteMood(id);
    }
}
