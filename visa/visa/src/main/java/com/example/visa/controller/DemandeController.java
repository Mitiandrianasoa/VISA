package com.example.visa.controller;

import com.example.visa.dto.DemandeDTO;
import com.example.visa.entities.Demande;
import com.example.visa.entities.Nationalite;
import com.example.visa.entities.PieceJustificative;
import com.example.visa.entities.SituationFamiliale;
import com.example.visa.entities.TypeVisa;
import com.example.visa.service.DemandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DemandeController {

    private final DemandeService demandeService;

    @GetMapping("/nationalites")
    public ResponseEntity<List<Nationalite>> getAllNationalites() {
        return ResponseEntity.ok(demandeService.getAllNationalites());
    }

    @GetMapping("/situations-familiales")
    public ResponseEntity<List<SituationFamiliale>> getAllSituationsFamiliales() {
        return ResponseEntity.ok(demandeService.getAllSituationsFamiliales());
    }

    @GetMapping("/types-visa")
    public ResponseEntity<List<TypeVisa>> getAllTypesVisa() {
        return ResponseEntity.ok(demandeService.getAllTypesVisa());
    }

    @GetMapping("/pieces/{typeVisaId}")
    public ResponseEntity<List<PieceJustificative>> getPiecesByTypeVisa(@PathVariable Integer typeVisaId) {
        return ResponseEntity.ok(demandeService.getPiecesByTypeVisa(typeVisaId));
    }

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody DemandeDTO demandeDTO) {
        try {
            Demande demande = demandeService.createDemande(demandeDTO);
            return ResponseEntity.ok(new Object() {
                public boolean success = true;
                public String message = "Demande créée avec succès";
                public Integer id = demande.getId();
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        }
    }

    @GetMapping
    public ResponseEntity<List<Demande>> getAllDemandes() {
        return ResponseEntity.ok(demandeService.getAllDemandes());
    }
}
