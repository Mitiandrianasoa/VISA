package com.example.visa.service;

import com.example.visa.dto.DemandeDTO;
import com.example.visa.dto.VisaTransformableDTO;
import com.example.visa.entities.*;
import com.example.visa.repository.HistoriqueStatutDemandeRepository;
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
    private final HistoriqueStatutDemandeRepository historiqueStatutDemandeRepository;

    public List<Nationalite> getAllNationalites() {
        return nationaliteRepository.findAll();
    }

    public List<SituationFamiliale> getAllSituationsFamiliales() {
        return situationFamilialeRepository.findAll();
    }

    public List<TypeVisa> getAllTypesVisa() {
        return typeVisaRepository.findAll();
    }

    public List<TypeDemande> getAllTypesDemande() {
        return typeDemandeRepository.findAll();
    }

    public List<Demandeur> rechercherDemandeur(String nom, String prenom) {
        return demandeurRepository.findByNomAndPrenomContainingIgnoreCase(nom, prenom);
    }

    public Demandeur getDemandeurById(Integer id) {
        Demandeur demandeur = demandeurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demandeur non trouvé avec l'ID: " + id));
        return demandeur;
    }

    public List<Passeport> getPasseportsByDemandeurId(Integer demandeurId) {
        return passeportRepository.findByIdDemandeurId(demandeurId);
    }

    public List<Demande> getDemandesByDemandeurId(Integer demandeurId) {
        return demandeRepository.findByDemandeurId(demandeurId);
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
        
        // Vérifier si c'est un duplicata avec un demandeur existant
        Demandeur demandeur;
        Passeport passeport;
        boolean demandeurExiste = false;
        
        if (demandeDTO.getIdDemandeur() != null) {
            // Utiliser le demandeur existant (duplicata)
            demandeur = getDemandeurById(demandeDTO.getIdDemandeur());
            demandeurExiste = true;
            
            // Utiliser le passeport existant du demandeur
            List<Passeport> passeports = getPasseportsByDemandeurId(demandeur.getId());
            if (passeports != null && !passeports.isEmpty()) {
                passeport = passeports.get(passeports.size() - 1); // Dernier passeport
            } else {
                throw new RuntimeException("Aucun passeport trouvé pour ce demandeur");
            }
        } else {
            // Créer un nouveau demandeur
            demandeur = new Demandeur();
            demandeur.setNom(demandeDTO.getNom());
            demandeur.setPrenom(demandeDTO.getPrenom());
            demandeur.setDateNaissance(demandeDTO.getDateNaissance());
            demandeur.setIdNationalite(nationaliteRepository.findById(demandeDTO.getIdNationalite()).orElseThrow());
            demandeur.setIdSituationFamiliale(
                    situationFamilialeRepository.findById(demandeDTO.getIdSituationFamiliale()).orElseThrow());
            demandeur.setAdresseMada(demandeDTO.getAdresseMada());
            demandeur.setContact(demandeDTO.getContact());
            demandeur.setEmail(demandeDTO.getEmail());
            demandeur = demandeurRepository.save(demandeur);
            
            // Créer le passeport
            passeport = new Passeport();
            passeport.setNumero(demandeDTO.getNumeroPasseport());
            passeport.setIdDemandeur(demandeur);
            passeport.setDateDelivrance(demandeDTO.getDateDelivrancePasseport());
            passeport.setDateExpiration(demandeDTO.getDateExpirationPasseport());
            passeport = passeportRepository.save(passeport);
        }

        // Créer le visa transformable si applicable
        VisaTransformable visaTransformable = null;
        if (demandeDTO.getVisaTransformable() != null) {
            visaTransformable = new VisaTransformable();
            visaTransformable.setIdPasseport(passeport);
            visaTransformable.setNumero(demandeDTO.getVisaTransformable().getNumero());
            visaTransformable.setDateEntreeTerritoire(demandeDTO.getVisaTransformable().getDateEntreeTerritoire());
            visaTransformable.setLieuEntreeTerritoire(demandeDTO.getVisaTransformable().getLieuEntreeTerritoire());
            visaTransformable
                    .setNumeroVisaTransformable(demandeDTO.getVisaTransformable().getNumeroVisaTransformable());
            visaTransformable.setDateSortieTerritoire(demandeDTO.getVisaTransformable().getDateSortieTerritoire());
            visaTransformable = visaTransformableRepository.save(visaTransformable);
        }

        // Créer la demande
        Demande demande = new Demande();
        demande.setIdVisaTransformable(visaTransformable);
        demande.setIdDemandeur(demandeur); // Associer directement le demandeur
        
        // Déterminer le statut selon les règles de duplicata
        Integer statutId;
        if (demandeDTO.getIdTypeDemande() != null && demandeDTO.getIdTypeDemande() == 2) {
            // Type de demande = Duplicata : toujours approuvé automatiquement
            statutId = 3; // Approuvée
        } else {
            // Nouvelle demande classique : statut En attente (1)
            statutId = 1;
        }
        
        System.out.println("Statut ID: " + statutId);
        demande.setIdStatut(statutDemandeRepository.findById(statutId).orElseThrow(() -> new RuntimeException("Statut non trouvé avec ID: " + statutId)));
        
        demande.setDateDemande(Instant.now());
        System.out.println("Type Visa ID: " + demandeDTO.getIdTypeVisa());
        demande.setIdTypeVisa(typeVisaRepository.findById(demandeDTO.getIdTypeVisa()).orElseThrow(() -> new RuntimeException("Type visa non trouvé avec ID: " + demandeDTO.getIdTypeVisa())));
        System.out.println("Type Demande ID: " + demandeDTO.getIdTypeDemande());
        demande.setIdTypeDemande(typeDemandeRepository.findById(demandeDTO.getIdTypeDemande()).orElseThrow(() -> new RuntimeException("Type demande non trouvé avec ID: " + demandeDTO.getIdTypeDemande())));
        demande = demandeRepository.save(demande);

        // Créer l'entrée dans l'historique des statuts pour la création (toutes les demandes)
        creerHistoriqueCentralise(demande, demande.getIdStatut().getId(), "création demande");
        
        System.out.println("HISTORIQUE CENTRALISÉ - création demande - Demande ID: " + demande.getId() + " - Statut: " + demande.getIdStatut().getLibelle());

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

    public Demande getDemandeById(Integer id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + id));
    }

    public void validerDemandes(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Aucune demande à valider");
        }

        // Récupérer le statut "Validé" (ID = 2, ajuster selon votre base de données)
        StatutDemande statutValide = statutDemandeRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Statut 'Validé' non trouvé"));

        for (Integer id : ids) {
            Demande demande = demandeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée avec l'ID: " + id));
            
            // Vérifier si la demande est déjà validée
            if (demande.getIdStatut() != null && demande.getIdStatut().getId() == 2) {
                System.out.println("La demande " + id + " est déjà validée - Ignorer");
                continue; // Passer à la demande suivante
            }
            
            creerHistoriqueCentralise(demande, statutValide.getId(), "validation");

            // Mettre à jour le statut de la demande
            demande.setIdStatut(statutValide);
            demandeRepository.save(demande);
            
            System.out.println("Historique du statut créé pour la demande " + id + " - Statut: VALIDÉ");
        }
    }

    /**
     * Méthode centralisée pour créer un historique pour n'importe quelle action
     * Crée systématiquement un historique pour TOUTES les actions
     */
    public void creerHistoriqueCentralise(Demande demande, Integer nouveauStatutId, String nomOperation) {
        // Créer systématiquement l'historique sans vérifier s'il existe déjà
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setIdDemande(demande);
        
        // Récupérer le statut correspondant
        StatutDemande statut = statutDemandeRepository.findById(nouveauStatutId)
                .orElseThrow(() -> new RuntimeException("Statut non trouvé avec l'ID: " + nouveauStatutId));
        historique.setIdStatutDemande(statut);
        
        // Utiliser l'administrateur par défaut (ID 1)
        Administrateur adminParDefaut = new Administrateur();
        adminParDefaut.setId(1);
        historique.setIdAdministrateur(adminParDefaut);
        historique.setDateUpdate(Instant.now());
        
        // Sauvegarder l'historique
        historiqueStatutDemandeRepository.save(historique);
        
        // Mettre à jour le statut de la demande
        demande.setIdStatut(statut);
        demandeRepository.save(demande);
        
        System.out.println("HISTORIQUE CENTRALISÉ - " + nomOperation + " - Demande ID: " + demande.getId() + " - Statut: " + statut.getLibelle());
    }

    private boolean historiqueExisteDeja(Integer demandeId, Integer statutId) {
        List<HistoriqueStatutDemande> historiques = historiqueStatutDemandeRepository.findByDemandeIdOrderByDateUpdateDesc(demandeId);
        return historiques.stream()
                .anyMatch(h -> h.getIdStatutDemande() != null && h.getIdStatutDemande().getId().equals(statutId));
    }

    public List<HistoriqueStatutDemande> getHistoriqueByDemandeId(Integer demandeId) {
        return historiqueStatutDemandeRepository.findByDemandeIdOrderByDateUpdateDesc(demandeId);
    }

    public List<HistoriqueStatutDemande> getAllHistorique() {
        return historiqueStatutDemandeRepository.findAllOrderByDateUpdateDesc();
    }
}
