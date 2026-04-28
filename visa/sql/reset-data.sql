-- Script de réinitialisation des données avec TRUNCATE
-- TRUNCATE supprime toutes les données et réinitialise automatiquement les séquences

-- Suppression des données (dans l'ordre inverse des dépendances)
TRUNCATE TABLE demande_piece CASCADE;
TRUNCATE TABLE demande CASCADE;
TRUNCATE TABLE passeport CASCADE;
TRUNCATE TABLE demandeur CASCADE;
TRUNCATE TABLE visa_transformable CASCADE;
TRUNCATE TABLE piece_specifique_type_visa CASCADE;
TRUNCATE TABLE piece_justificative CASCADE;
TRUNCATE TABLE type_demande CASCADE;
TRUNCATE TABLE statut_demande CASCADE;
TRUNCATE TABLE type_visa CASCADE;
TRUNCATE TABLE situation_familiale CASCADE;
TRUNCATE TABLE nationalite CASCADE;

-- Réinsertion des données (inclut la modification de la notice de renseignement comme facultative)
INSERT INTO type_visa (id, code, libelle) VALUES
(1, 'INV', 'Visa investisseur'),
(2, 'TRAV', 'Visa travailleur');

INSERT INTO type_demande (id, code, libelle) VALUES
(1, 'NOUV', 'Nouvelle demande'),
(2, 'DUP', 'Duplicata');

INSERT INTO statut_demande (id, code, libelle) VALUES
(1, 'EN_ATTENTE', 'En attente'),
(2, 'VALIDE', 'Validée'),
(3, 'APPROUVEE', 'Approuvée');

INSERT INTO nationalite (id, libelle) VALUES
(1, 'Française'),
(2, 'Malagasy'),
(3, 'Chinoise');

INSERT INTO situation_familiale (id, libelle) VALUES
(1, 'Célibataire'),
(2, 'Marié(e)');

INSERT INTO piece_justificative (id, code, libelle, commun, obligatoire) VALUES
-- COMMUNES
(1, 'PHOTO', '02 photos d''identité', true, true),
(2, 'FORM', 'Notice de renseignement', true, false),
(3, 'DEMANDE', 'Lettre de demande au ministère', true, true),
(4, 'VISA_COPY', 'Copie du visa valide', true, true),
(5, 'PASSPORT_COPY', 'Copie du passeport', true, true),
(6, 'RESIDENCE_COPY', 'Copie carte résident en cours de validité', true, true),
(7, 'RESIDENCE_CERT', 'Certificat de résidence à Madagascar', true, true),
(8, 'CASIER', 'Extrait de casier judiciaire < 3 mois', true, true),
-- INVESTISSEUR
(9, 'STATUT_SOCIETE', 'Statuts de la société', false, true),
(10, 'RCS', 'Registre du commerce', false, true),
(11, 'CARTE_FISCALE', 'Carte fiscale', false, true),
-- TRAVAILLEUR
(12, 'AUTORISATION_TRAVAIL', 'Autorisation d''emploi', false, true),
(13, 'ATTESTATION_EMPLOI', 'Attestation de l''employeur', false, true);

INSERT INTO piece_specifique_type_visa VALUES
(1, 9),  -- STATUT_SOCIETE
(1, 10), -- RCS
(1, 11), -- CARTE_FISCALE
(2, 12), -- AUTORISATION_TRAVAIL
(2, 13); -- ATTESTATION_EMPLOI

INSERT INTO demandeur 
(id, nom, prenom, date_naissance, id_nationalite, id_situation_familiale, adresse_mada, contact, email)
VALUES
(1, 'Dupont', 'Jean', '1990-05-10', 1, 2, 'Antananarivo', '0341234567', 'jean.dupont@email.com'),
(2, 'Li', 'Wei', '1985-09-20', 3, 1, 'Toamasina', '0329876543', 'li.wei@email.com');

INSERT INTO passeport (id, numero, id_demandeur, date_delivrance, date_expiration) VALUES
(1, 'P123456', 1, '2020-01-01', '2030-01-01'),
(2, 'P654321', 2, '2021-06-01', '2031-06-01');

INSERT INTO demande (id, id_statut, date_demande, id_type_visa, id_type_demande) VALUES
(1, 1, CURRENT_TIMESTAMP, 1, 1),
(2, 1, CURRENT_TIMESTAMP, 2, 1);

INSERT INTO demande_piece VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), 
(1, 6), (1, 7), (1, 8),  -- Pièces communes
(1, 9), (1, 10), (1, 11), -- Pièces investisseur
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), 
(2, 6), (2, 7), (2, 8),  -- Pièces communes
(2, 12), (2, 13); -- Pièces travailleur
