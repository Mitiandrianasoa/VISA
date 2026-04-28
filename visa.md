VISA transformable:
    - demande VISA long sejour
        - VISA: investisseur, travailleur, ...
contrainte:
    - VISA transformable doit etre suivi par le gouvernement 
    - saisir information sur le VISA transformable

demarche
    - enregistrement de demande de VISA transformable(ex validation 3 mois):
    - avant le delai , demande de VISA investisseur, travailleur, ... 
        - type de demande:
                - nouveau titre
                - duplicata(carte resident) avec ou sans donnee anterieur(besoin de resaisie)
                - transfert de VISA avec ou sans donnee anterieur(besoin de resaisie)
    - verification du type de VISA par le ministere
    - obtention de carte resident/carte VISA ou duplicata ou transfert de VISA dans le nouveau passeport

info dans le VISA transformable:
    - reference
    - date entree Mada
    - date expiration VISA transformable
    - etat civil    
        - nom 
        - prenom
        - nom de jeune fille
        - date et lieu de naissance
        - numero de passeport valide (+15 mois apres la date de depart prevu)
        - nationalite
        - adresse Madagascar
        - situation familliale
        - numero passeport
        - donnees de contact:
            - email
            - numero portable 
    -(a chercher...):    
        - info:
        - par type/ status:
            - investisseur et travailleur uniquement d'abord

15-04-2026 

Team Lead: [Mitia 3145]

sprint 1:[fiche de renseignement, transcription de donnees pour une demande de nouveau titre]
    - saisie des informations 
    - saisir les informations des personnes qui demandent leur visa nouveau titre mais qui ne sont pas encore dans notre base de données (formulaire hafa mihitsy)
    - case a cocher pour les pieces a fournir [https://www.madagascar-services.com/blog/fr/2017/03/07/carte-de-resident-visa-biometrique-a-madagascar/]


Tsanta
Sprint 2 : [Cas DUPLICATA Carte de résidence] 
    - si Carte de résidence PERDU et sans données antérieures :
        - refaire un dossier de VISA mais avec "Validé" à la fin

Duplicata — traitement des cas de perte et sans données antérieures.*
Une personne peut faire une demande de duplicata de carte de résident ou un transfert de visa en cas de perte. Les deux cas partagent le même formulaire, avec un choix au départ :
- *Passeport perdu* → Transfert de visa vers le nouveau passeport. L'information supplémentaire requise est uniquement le nouveau numéro de passeport.
- *Carte de résident perdue* → Demande de duplicata de carte de résident.

Dans les deux cas, si la personne est déjà dans le système, ses données sont pré-remplies.

Si la personne n'a *aucune donnée antérieure* dans le système, elle saisit toutes les informations from scratch, comme pour une nouvelle demande. Le statut sera directement *Approuvée* (et non "Document créé" comme au Sprint 1), car il s'agit d'une régularisation administrative.    

Aina 
Sprint 3 : [PDF]
    - impression en pdf des détails d'un dossier de visa en récépicé
    - uploadé les pièces sur le dossier
    - sauve
    - après tous les uploads, l'état du dossier devient : "Scan terminé"
        - on ne pourra plus faire de modification du dossier de visa


Mitia 
sprint 4: [frontend VUE]
    - frontend:
        - demande ->  generation QR code -> 
        - liste des historique de  demande et ses status par rapport un numero de passeport(par ordre chronologique) ou numero demande(historique de cette demande afficher en premier).
        - utilisation API depuis notre  spring boot. 