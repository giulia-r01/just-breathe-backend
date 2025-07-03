package it.epicode.just_breathe_backend.dto;

import it.epicode.just_breathe_backend.model.*;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDto {
    private Diario diario;
    private String messaggioDiario;

    private List<Evento> eventi;
    private String messaggioEvento;

    private List<ToDoList> tasks;
    private String messaggioToDo;

    private Respiro respiro;
    private String messaggioRespiro;

    private Mood mood;
    private List<Brano> brani;
    private String messaggioMood;

}
