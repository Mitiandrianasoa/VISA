package com.example.visa.dto.api;

import java.time.Instant;

public record DemandeListeItemDTO(
        Integer numeroDemande,
        String numeroPasseport,
        Instant dateDemande,
        String nom,
        String prenom,
        String typeVisa,
        String statut
) {}
