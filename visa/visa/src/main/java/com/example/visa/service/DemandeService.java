package com.example.visa.service;

import com.example.visa.dto.DemandeDTO;
import com.example.visa.dto.VisaTransformableDTO;
import com.example.visa.entities.*;
import com.example.visa.repository.*;
import com.example.visa.service.exception.PiecesIncompletesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeService {

    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final VisaTransformableRepository visaTransformableRepository;
    private final DemandeRepository demandeRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandePieceRepository demandePieceRepository;

    public List<Nationalite> getAllNationalites() {
        return nationaliteRepository.findAll();
    }

    public List<SituationFamiliale> getAllSituationsFamiliales() {
        return situationFamilialeRepository.findAll();
    }

    public List<TypeVisa> getAllTypesVisa() {
        return typeVisaRepository.findAll();
    }

    public List<PieceJustificative> getPiecesByTypeVisa(Integer typeVisaId) {
        return pieceJustificativeRepository.findPiecesByTypeVisa(typeVisaId);
    }

    public List<Demande> getAllDemandes() {
        return demandeRepository.findAll();
    }
    private void validatePiecesCompletes(DemandeDTO demandeDTO) {
        if (demandeDTO.getIdTypeVisa() == null) {
            throw new IllegalArgumentException("Type de visa obligatoire");
        }

        // Pièces attendues pour ce type de visa (communes + spécifiques)
        List<PieceJustificative> piecesAttendues = pieceJustificativeRepository
                .findPiecesByTypeVisa(demandeDTO.getIdTypeVisa());

        // On ne force que les pièces obligatoires
        Set<Integer> idsObligatoires = piecesAttendues.stream()
                .filter(p -> Boolean.TRUE.equals(p.getObligatoire()))
                .map(PieceJustificative::getId)
                .collect(Collectors.toSet());

        // Pièces fournies par le client
        Set<Integer> idsFournis = Optional.ofNullable(demandeDTO.getPieceIds())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Pièces manquantes
        Set<Integer> idsManquants = new HashSet<>(idsObligatoires);
        idsManquants.removeAll(idsFournis);

        if (!idsManquants.isEmpty()) {
            // message strict demandé:
            throw new PiecesIncompletesException("pieces incomplete");
        }
    }

    public Demande createDemande(DemandeDTO demandeDTO) {

        validatePiecesCompletes(demandeDTO); // <-- IMPORTANT: avant tout save
        // Créer le demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(demandeDTO.getNom());
        demandeur.setPrenom(demandeDTO.getPrenom());
        demandeur.setDateNaissance(demandeDTO.getDateNaissance());
        demandeur.setIdNationalite(nationaliteRepository.findById(demandeDTO.getIdNationalite()).orElseThrow());
        demandeur.setIdSituationFamiliale(situationFamilialeRepository.findById(demandeDTO.getIdSituationFamiliale()).orElseThrow());
        demandeur.setAdresseMada(demandeDTO.getAdresseMada());
        demandeur.setContact(demandeDTO.getContact());
        demandeur.setEmail(demandeDTO.getEmail());
        demandeur = demandeurRepository.save(demandeur);

        // Créer le passeport
        Passeport passeport = new Passeport();
        passeport.setNumero(demandeDTO.getNumeroPasseport());
        passeport.setIdDemandeur(demandeur);
        passeport.setDateDelivrance(demandeDTO.getDateDelivrancePasseport());
        passeport.setDateExpiration(demandeDTO.getDateExpirationPasseport());
        passeport = passeportRepository.save(passeport);

        // Créer le visa transformable si applicable
        VisaTransformable visaTransformable = null;
        if (demandeDTO.getVisaTransformable() != null) {
            visaTransformable = new VisaTransformable();
            visaTransformable.setIdPasseport(passeport);
            visaTransformable.setNumero(demandeDTO.getVisaTransformable().getNumero());
            visaTransformable.setDateEntreeTerritoire(demandeDTO.getVisaTransformable().getDateEntreeTerritoire());
            visaTransformable.setLieuEntreeTerritoire(demandeDTO.getVisaTransformable().getLieuEntreeTerritoire());
            visaTransformable.setNumeroVisaTransformable(demandeDTO.getVisaTransformable().getNumeroVisaTransformable());
            visaTransformable.setDateSortieTerritoire(demandeDTO.getVisaTransformable().getDateSortieTerritoire());
            visaTransformable = visaTransformableRepository.save(visaTransformable);
        }

        // Créer la demande
        Demande demande = new Demande();
        demande.setIdVisaTransformable(visaTransformable);
        demande.setIdStatut(statutDemandeRepository.findById(1).orElseThrow()); // En attente par défaut
        demande.setDateDemande(Instant.now());
        demande.setIdTypeVisa(typeVisaRepository.findById(demandeDTO.getIdTypeVisa()).orElseThrow());
        demande.setIdTypeDemande(typeDemandeRepository.findById(demandeDTO.getIdTypeDemande()).orElseThrow());
        demande = demandeRepository.save(demande);

        // Associer les pièces justificatives
        if (demandeDTO.getPieceIds() != null) {
            for (Integer pieceId : demandeDTO.getPieceIds()) {
                DemandePiece demandePiece = new DemandePiece();
                demandePiece.setIdDemande(demande.getId());
                demandePiece.setIdPiece(pieceId);
                demandePiece.setDemande(demande);
                demandePiece.setPiece(pieceJustificativeRepository.findById(pieceId).orElseThrow());
                demandePieceRepository.save(demandePiece);
            }
        }

        return demande;
    }
}
