package com.example.visa.controller;

import com.example.visa.entities.Nationalite;
import com.example.visa.entities.SituationFamiliale;
import com.example.visa.entities.TypeVisa;
import com.example.visa.entities.TypeDemande;
import com.example.visa.service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final DemandeService demandeService;

    @GetMapping("/demande/nouveau")
    public String showFormulaire(Model model) {
        List<Nationalite> nationalites = demandeService.getAllNationalites();
        List<SituationFamiliale> situationsFamiliales = demandeService.getAllSituationsFamiliales();
        List<TypeVisa> typesVisa = demandeService.getAllTypesVisa();
        List<TypeDemande> typesDemande = demandeService.getAllTypesDemande();
        
        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situationsFamiliales", situationsFamiliales);
        model.addAttribute("typesVisa", typesVisa);
        model.addAttribute("typesDemande", typesDemande);
        
        return "demande/formulaire-sprint2";
    }

    @GetMapping("/demande/duplicata")
    public String showDuplicata(Model model) {
        List<Nationalite> nationalites = demandeService.getAllNationalites();
        List<SituationFamiliale> situationsFamiliales = demandeService.getAllSituationsFamiliales();
        List<TypeVisa> typesVisa = demandeService.getAllTypesVisa();
        List<TypeDemande> typesDemande = demandeService.getAllTypesDemande();
        
        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situationsFamiliales", situationsFamiliales);
        model.addAttribute("typesVisa", typesVisa);
        model.addAttribute("typesDemande", typesDemande);
        
        return "demande/duplicata";
    }

    @GetMapping("/demande/liste")
    public String showListe() {
        return "demande/liste";
    }

    @GetMapping("/demande/scan")
    public String showScanPage(@org.springframework.web.bind.annotation.RequestParam(name = "id") Integer demandeId,
            Model model) {
        model.addAttribute("demandeId", demandeId);
        return "demande/scan";
    }
}