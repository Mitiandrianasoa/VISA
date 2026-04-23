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
        // Règle demandée: si on n'a pas encore le numéro passeport -> vide
        if (numeroPasseport == null || numeroPasseport.trim().isEmpty()) {
            return List.of();
        }
        return demandeListeApiRepository.rechercherPourApi(numeroPasseport.trim(), numeroDemande);
    }

    public List<DemandeListeItemDTO> rechercherParNumeroDemande(Integer numeroDemande) {
        if (numeroDemande == null) {
            return List.of();
        }
        return demandeListeApiRepository.rechercherParNumeroDemande(numeroDemande);
    }

}
