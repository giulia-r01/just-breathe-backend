package it.epicode.just_breathe_backend.controller;

import it.epicode.just_breathe_backend.dto.DashboardDto;
import it.epicode.just_breathe_backend.model.Utente;
import it.epicode.just_breathe_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "dashboard")
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public DashboardDto getDashBoard(){
        Utente utente = (Utente) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return dashboardService.getDashboard(utente);
    }
}
