package com.example.visa.controller;

import com.example.visa.entities.Demande;
import com.example.visa.entities.Demandeur;
import com.example.visa.entities.DocumentDemandeur;
import com.example.visa.repository.DemandeRepository;
import com.example.visa.repository.DocumentDemandeurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PhotoSignatureController {

    private final DemandeRepository demandeRepository;
    private final DocumentDemandeurRepository documentDemandeurRepository;

    @PostMapping("/{demandeId}/document")
    public ResponseEntity<?> saveDocument(
            @PathVariable Integer demandeId,
            @RequestBody Map<String, String> payload) {
        try {
            Demande demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

            Demandeur demandeur = demande.getIdDemandeur();
            if (demandeur == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Aucun demandeur associé à cette demande"));
            }

            String photo = payload.get("photo");
            String signature = payload.get("signature");

            if (photo == null || photo.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "La photo est requise"));
            }
            if (signature == null || signature.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "La signature est requise"));
            }

            Optional<DocumentDemandeur> existing = documentDemandeurRepository.findByIdDemandeurId(demandeur.getId());
            DocumentDemandeur doc = existing.orElseGet(DocumentDemandeur::new);

            if (doc.getId() == null) {
                doc.setIdDemandeur(demandeur);
                doc.setCreatedAt(Instant.now());
            }
            doc.setPhoto(photo);
            doc.setSignature(signature);
            doc.setUpdatedAt(Instant.now());

            documentDemandeurRepository.save(doc);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Photo et signature enregistrées avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{demandeId}/document")
    public ResponseEntity<?> getDocument(@PathVariable Integer demandeId) {
        try {
            Demande demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

            Demandeur demandeur = demande.getIdDemandeur();
            if (demandeur == null) {
                return ResponseEntity.ok(Map.of("success", true, "photo", "", "signature", ""));
            }

            Optional<DocumentDemandeur> doc = documentDemandeurRepository.findByIdDemandeurId(demandeur.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            if (doc.isPresent()) {
                response.put("photo", doc.get().getPhoto() != null ? doc.get().getPhoto() : "");
                response.put("signature", doc.get().getSignature() != null ? doc.get().getSignature() : "");
            } else {
                response.put("photo", "");
                response.put("signature", "");
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
