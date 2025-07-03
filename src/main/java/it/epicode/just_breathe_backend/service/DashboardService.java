package it.epicode.just_breathe_backend.service;

import it.epicode.just_breathe_backend.dto.DashboardDto;
import it.epicode.just_breathe_backend.enumeration.TipoTask;
import it.epicode.just_breathe_backend.model.Brano;
import it.epicode.just_breathe_backend.model.Evento;
import it.epicode.just_breathe_backend.model.ToDoList;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    DiarioRepository diarioRepository;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    ToDoRepository toDoRepository;

    @Autowired
    RespiroRepository respiroRepository;

    @Autowired
    MoodRepository moodRepository;

    @Autowired
    BranoRepository branoRepository;


    public DashboardDto getDashboard(Utente utente){
        DashboardDto dashboardDto = new DashboardDto();

        //respiro
        respiroRepository.findTopByOrderByDataCreazioneDesc().ifPresent(dashboardDto::setRespiro);

        //eventi
        List<Evento> eventiSalvati = eventoRepository.findByUtenteOrderByDataEventoDesc(utente);
        if (eventiSalvati.isEmpty()) {
            dashboardDto.setMessaggioEvento("Al momento non hai salvato nessun evento a cui sei interessato");
        } else {
            dashboardDto.setEventi(eventiSalvati);
            dashboardDto.setMessaggioEvento(null);
        }

        //diario
        diarioRepository.findTopByUtenteIdOrderByDataUltimaModificaDesc(utente.getId()).ifPresentOrElse(diario -> {
                    dashboardDto.setDiario(diario);
                    dashboardDto.setMessaggioDiario(null);
                },
                () -> dashboardDto.setMessaggioDiario("Non hai ancora scritto nulla nel tuo diario, comincia ora!"));


        //ToDoList
        List<ToDoList> tasks = toDoRepository.findByUtenteIdAndTipoTaskNotOrderByDataCreazioneTaskAsc(utente.getId(), TipoTask.FATTO);
        if (tasks.isEmpty()) {
            dashboardDto.setMessaggioToDo("Non hai ancora task da completare, organizza il tuo tempo!");
        } else {
            dashboardDto.setTasks(tasks);
            dashboardDto.setMessaggioToDo(null);
        }

        //mood
        moodRepository.findTopByUtenteIdOrderByDataCreazioneDesc(utente.getId())
                .ifPresentOrElse(mood -> {
                    dashboardDto.setMood(mood);
                    List<Brano> brani = branoRepository.findByMoodId(mood.getId());
                    dashboardDto.setBrani(brani);
                    dashboardDto.setMessaggioMood(null);
                }, () -> dashboardDto.setMessaggioMood("Qual è il tuo mood oggi? Crea la tua playlist!"));

        return dashboardDto;
    }
}
