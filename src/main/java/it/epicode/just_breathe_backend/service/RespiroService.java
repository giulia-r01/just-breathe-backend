package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.RespiroDto;
import it.epicode.just_breathe_backend.exceptions.NotFoundException;
import it.epicode.just_breathe_backend.model.Respiro;
import it.epicode.just_breathe_backend.repository.RespiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RespiroService {

    @Autowired
    private RespiroRepository respiroRepository;

    public Respiro saveRespiro(RespiroDto respiroDto) {
        Respiro respiro = new Respiro();

        respiro.setNome(respiroDto.getNome());
        respiro.setDescrizione(respiroDto.getDescrizione());
        respiro.setDataCreazione(LocalDateTime.now());
        respiro.setInspiraSecondi(respiroDto.getInspiraSecondi());
        respiro.setTrattieniSecondi(respiroDto.getTrattieniSecondi());
        respiro.setEspiraSecondi(respiroDto.getEspiraSecondi());
        respiro.setCategoria(respiroDto.getCategoria());
        return respiroRepository.save(respiro);
    }

    public Respiro getRespiro(Long id) throws NotFoundException {
        Respiro respiro = respiroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appuntamento non trovato"));

        return respiro;
    }

    public List<Respiro> getAllRespiri() {
        return respiroRepository.findAll();
    }

    public Respiro updateRespiro(Long id, RespiroDto respiroDto) throws NotFoundException {
        Respiro respiro = respiroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Respiro non trovato"));

        respiro.setNome(respiroDto.getNome());
        respiro.setDescrizione(respiroDto.getDescrizione());
        respiro.setInspiraSecondi(respiroDto.getInspiraSecondi());
        respiro.setTrattieniSecondi(respiroDto.getTrattieniSecondi());
        respiro.setEspiraSecondi(respiroDto.getEspiraSecondi());
        respiro.setCategoria(respiroDto.getCategoria());

        return respiroRepository.save(respiro);
    }

    public void deleteRespiro(Long id) throws NotFoundException {
        Respiro respiro = respiroRepository.findById(id).orElseThrow(() -> new NotFoundException("Respiro non trovato"));
        respiroRepository.delete(respiro);
    }
}
