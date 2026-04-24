package com.example.visa.controller;

import com.example.visa.entities.Nationalite;
import com.example.visa.entities.SituationFamiliale;
import com.example.visa.entities.TypeVisa;
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
        
        model.addAttribute("nationalites", nationalites);
        model.addAttribute("situationsFamiliales", situationsFamiliales);
        model.addAttribute("typesVisa", typesVisa);
        
        return "demande/formulaire-QR";
    }

    @GetMapping("/demande/liste")
    public String showListe() {
        return "demande/liste";
    }
}
