package com.example.visa.controller;

import com.example.visa.dto.api.DemandeListeItemDTO;
import com.example.visa.service.DemandeListeApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-liste")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DemandeListeApiController {

    private final DemandeListeApiService demandeListeApiService;

    @GetMapping
    public ResponseEntity<List<DemandeListeItemDTO>> getDemandes(
            @RequestParam(required = false) String numeroPasseport,
            @RequestParam(required = false) Integer numeroDemande
    ) {
        return ResponseEntity.ok(
                demandeListeApiService.rechercher(numeroPasseport, numeroDemande)
        );
    }

    @GetMapping("/par-numero-demande")
    public ResponseEntity<List<DemandeListeItemDTO>> getDemandesParNumeroDemande(
            @RequestParam Integer numeroDemande
    ) {
        return ResponseEntity.ok(
                demandeListeApiService.rechercherParNumeroDemande(numeroDemande)
        );
    }

}
