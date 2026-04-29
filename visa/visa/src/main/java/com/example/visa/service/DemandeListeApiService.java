package com.example.visa.service;

import com.example.visa.dto.api.DemandeListeItemDTO;
import com.example.visa.repository.DemandeListeApiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandeListeApiService {

    private final DemandeListeApiRepository demandeListeApiRepository;

    public List<DemandeListeItemDTO> rechercher(String numeroPasseport, Integer numeroDemande) {
        String numeroPasseportNettoye = (numeroPasseport == null) ? null : numeroPasseport.trim();

        // Critère 1 : recherche par numéro passeport (+ option prioriser une demande)
        if (numeroPasseportNettoye != null && !numeroPasseportNettoye.isEmpty()) {
            return demandeListeApiRepository.rechercherPourApi(numeroPasseportNettoye, String.valueOf(numeroDemande));
        }

        // Critère 2 : si passeport absent mais numéro demande présent
        if (numeroDemande != null) {
            return demandeListeApiRepository.rechercherParNumeroDemande(numeroDemande);
        }

        return List.of();
    }

    public List<DemandeListeItemDTO> rechercherParNumeroDemande(Integer numeroDemande) {
        if (numeroDemande == null) {
            return List.of();
        }
        return demandeListeApiRepository.rechercherParNumeroDemande(numeroDemande);
    }

}
