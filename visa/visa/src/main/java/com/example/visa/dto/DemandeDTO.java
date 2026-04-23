package com.example.visa.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DemandeDTO {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Integer idNationalite;
    private Integer idSituationFamiliale;
    private String adresseMada;
    private String contact;
    private String email;
    
    // ID du demandeur existant (pour duplicata)
    private Integer idDemandeur;
    
    // Passeport
    private String numeroPasseport;
    private LocalDate dateDelivrancePasseport;
    private LocalDate dateExpirationPasseport;
    
    // Visa transformable (optionnel)
    private VisaTransformableDTO visaTransformable;
    
    // Demande
    private Integer idTypeVisa;
    private Integer idTypeDemande;
    
    // Pièces justificatives (cases à cocher)
    private List<Integer> pieceIds;
}
