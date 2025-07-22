package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.BranoDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.service.BranoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "brani")
public class BranoController {

    @Autowired
    private BranoService branoService;

    @PostMapping("/{moodId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Brano addBrano(@PathVariable Long moodId, @RequestBody @Validated BranoDto branoDto) throws NotFoundException {
        return branoService.addBranoToMood(moodId, branoDto);
    }

    @GetMapping("/mood/{moodId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public List<Brano> getBraniByMood(@PathVariable Long moodId) throws NotFoundException {
        return branoService.getBraniByMood(moodId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Brano getBranoById(@PathVariable Long id) throws NotFoundException {
        return branoService.getBranoById(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Page<Brano> getAllBrani(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return branoService.getAllBrani(page, size, sortBy);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Brano updateBrano(@PathVariable Long id, @RequestBody @Validated BranoDto branoDto)
            throws NotFoundException {
        return branoService.updateBrano(id, branoDto);
    }

    @PatchMapping("/{branoId}/mood/{nuovoMoodId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Brano cambiaMood(@PathVariable Long branoId, @PathVariable Long nuovoMoodId) throws NotFoundException {
        return branoService.cambiaMoodDelBrano(branoId, nuovoMoodId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public void deleteBrano(@PathVariable Long id) throws NotFoundException {
        branoService.deleteBrano(id);
    }
}
