-- Script d'insertion des données de référence
-- Ces données sont insérées sans réinitialiser les séquences
-- Les données existantes ne sont pas modifiées

-- Insertion des données de référence (avec ON CONFLICT DO NOTHING pour éviter les doublons)

INSERT INTO situation_familiale (id, libelle) VALUES
(1, 'Célibataire'),
(2, 'Marié(e)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO nationalite (id, libelle) VALUES
(1, 'Française'),
(2, 'Malagasy'),
(3, 'Chinoise')
ON CONFLICT (id) DO NOTHING;

INSERT INTO type_visa (id, code, libelle) VALUES
(1, 'INV', 'Visa investisseur'),
(2, 'TRAV', 'Visa travailleur')
ON CONFLICT (id) DO NOTHING;

INSERT INTO type_demande (id, code, libelle) VALUES
(1, 'NOUV', 'Nouvelle demande')
ON CONFLICT (id) DO NOTHING;

INSERT INTO statut_demande (id, code, libelle) VALUES
(1, 'EN_ATTENTE', 'En attente'),
(2, 'VALIDE', 'Validée')
ON CONFLICT (id) DO NOTHING;

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
(13, 'ATTESTATION_EMPLOI', 'Attestation de l''employeur', false, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO piece_specifique_type_visa VALUES
(1, 9),  -- STATUT_SOCIETE
(1, 10), -- RCS
(1, 11), -- CARTE_FISCALE
(2, 12), -- AUTORISATION_TRAVAIL
(2, 13)  -- ATTESTATION_EMPLOI
ON CONFLICT DO NOTHING;
