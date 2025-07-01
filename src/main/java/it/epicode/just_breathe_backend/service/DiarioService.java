package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.DiarioDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.exceptions.UnauthorizedException;
import it.epicode.just_breathe_backend.model.Diario;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.DiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DiarioService {

    @Autowired
    private DiarioRepository diarioRepository;

    public Diario saveDiario(DiarioDto diarioDto){
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Diario diario = new Diario();
        diario.setTitolo(diarioDto.getTitolo());
        diario.setContenuto(diarioDto.getContenuto());
        diario.setDataInserimento(LocalDate.now());
        diario.setDataUltimaModifica(LocalDate.now());
        diario.setUtente(utenteAutenticato);

        return diarioRepository.save(diario);
    }


    public Diario getDiario(Long id) throws NotFoundException {
        return diarioRepository.findById(id).orElseThrow(()-> new NotFoundException("Voce del diario non trovata"));
    }

    public Page<Diario> getAllDiarioUtente(int page, int size, String sortBy){
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return diarioRepository.findByUtente(utenteAutenticato, pageable);
    }

    public Diario updateDiario(Long id, DiarioDto diarioDto) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!utenteAutenticato.getRuolo().name().equals("USER") && utenteAutenticato.getId() != id) {
            throw new UnauthorizedException("Non puoi modificare il diario di un altro utente.");
        }

        Diario diario = getDiario(id);
        diario.setTitolo(diarioDto.getTitolo());
        diario.setContenuto(diarioDto.getContenuto());
        diario.setDataUltimaModifica(LocalDate.now());

        return diarioRepository.save(diario);
    }

    public void deleteDiario(Long id) throws NotFoundException {
        Utente utenteAutenticato = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!utenteAutenticato.getRuolo().name().equals("USER") && utenteAutenticato.getId() != id) {
            throw new UnauthorizedException("Non puoi eliminare il diario di un altro utente.");
        }

        Diario diario = getDiario(id);
        diarioRepository.delete(diario);
    }

}
