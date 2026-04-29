package com.example.visa.dto.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class DemandeListeItemDTO {
    private Integer id;
    private String numeroDemande;
    private Instant dateDemande;
    private String statut;
    private String numeroPasseport;
    private String nom;
    private String prenom;
    private String typeVisa;

    // Constructeur pour la recherche générale (rechercherPourApi)
    public DemandeListeItemDTO(Integer id, String numeroDemande, Instant dateDemande, String statut, String numeroPasseport) {
        this.id = id;
        this.numeroDemande = numeroDemande;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.numeroPasseport = numeroPasseport;
    }

    // Constructeur pour la recherche par numéro de demande (historique)
    public DemandeListeItemDTO(Integer id, String numeroPasseport, Instant dateDemande, String nom, String prenom, String typeVisa, String statut) {
        this.id = id;
        this.numeroPasseport = numeroPasseport;
        this.dateDemande = dateDemande;
        this.nom = nom;
        this.prenom = prenom;
        this.typeVisa = typeVisa;
        this.statut = statut;
    }
}
