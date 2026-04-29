-- ========================================
-- DONNÉES DE TEST SIMPLIFIÉES ET FONCTIONNELLES
-- ========================================

-- STATUTS
INSERT INTO statut_demande (id, code, libelle) VALUES
(1, 'EN_ATTENTE', 'En attente'),
(2, 'VALIDE', 'Validée'),
(3, 'APPROUVEE', 'Approuvée'),
(4, 'SCAN', 'Scan terminé');

-- TYPES DEMANDE
INSERT INTO type_demande (id, code, libelle) VALUES
(1, 'NOUV', 'Nouvelle demande'),
(2, 'DUP', 'Duplicata');

-- TYPES VISA
INSERT INTO type_visa (id, code, libelle) VALUES
(1, 'INVEST', 'Visa investisseur'),
(2, 'TRAV', 'Visa travailleur');

-- NATIONALITÉS
INSERT INTO nationalite (id, libelle) VALUES
(1, 'Malagasy'),
(2, 'Français'),
(3, 'Chinois');

-- SITUATIONS FAMILIALES
INSERT INTO situation_familiale (id, libelle) VALUES
(1, 'Célibataire'),
(2, 'Marie(e)');

-- PIÈCES JUSTIFICATIVES (codes courts)
INSERT INTO piece_justificative (id, code, libelle, commun, obligatoire) VALUES
(1, 'CNI', 'CNI', true, true),
(2, 'PASS', 'Passeport', true, true),
(3, 'PHOTO', 'Photo', true, true),
(4, 'CV', 'CV', true, true),
(5, 'LETTR', 'Lettre', true, true),
(6, 'RESID', 'Residence', true, true),
(7, 'NAISS', 'Naissance', true, true),
(8, 'CASIER', 'Casier', true, true),
(9, 'STATUT', 'Statut societe', false, false),
(10, 'RCS', 'RCS', false, false),
(11, 'FISCAL', 'Carte fiscale', false, false),
(12, 'AUTOR', 'Autorisation travail', false, false),
(13, 'ATTEST', 'Attestation emploi', false, false);

-- LIAISON PIÈCES / TYPE VISA
INSERT INTO piece_specifique_type_visa (id, id_type_visa, id_piece_justificative) VALUES
(1, 1, 9), (2, 1, 10), (3, 1, 11), -- Investisseur
(4, 2, 12), (5, 2, 13); -- Travailleur

-- ADMINISTRATEUR
INSERT INTO administrateur (id, nom, mot_de_passe, email, login) VALUES
(1, 'Admin', 'admin123', 'admin@visa.com', 'admin');

-- DEMANDEURS
INSERT INTO demandeur (id, nom, prenom, date_naissance, id_nationalite, id_situation_familiale, adresse_mada, contact, email) VALUES
(1, 'Dupont', 'Jean', '1990-05-10', 1, 2, 'Antananarivo', '0341234567', 'jean@email.com'),
(2, 'Li', 'Wei', '1985-09-20', 3, 1, 'Toamasina', '0329876543', 'wei@email.com'),
(3, 'Rakoto', 'Miry', '1988-03-15', 2, 1, 'Fianarantsoa', '0345556667', 'miry@email.com'),
(4, 'Randria', 'Luc', '1992-11-22', 1, 2, 'Mahajanga', '0327778889', 'luc@email.com'),
(5, 'Andria', 'Sonia', '1995-07-08', 2, 1, 'Toamasina', '0334455577', 'sonia@email.com'),
(6, 'Rabe', 'Jean', '1991-12-15', 1, 2, 'Antsirabe', '0322348899', 'jean.rabe@email.com');

-- PASSEPORTS
INSERT INTO passeport (id, numero, id_demandeur, date_delivrance, date_expiration) VALUES
(1, 'P123456', 1, '2020-01-01', '2030-01-01'),
(2, 'P654321', 2, '2021-06-01', '2031-06-01'),
(3, 'P789012', 3, '2019-03-15', '2029-03-15'),
(4, 'P345678', 4, '2020-11-22', '2030-11-22'),
(5, 'P901234', 5, '2021-07-08', '2031-07-08'),
(6, 'P567890', 6, '2022-12-15', '2032-12-15');

-- DEMANDES
INSERT INTO demande (id, id_statut, date_demande, id_type_visa, id_type_demande, id_demandeur) VALUES
(1, 1, CURRENT_TIMESTAMP, 1, 1, 1),  -- En attente
(2, 1, CURRENT_TIMESTAMP, 2, 1, 2),  -- En attente
(3, 2, CURRENT_TIMESTAMP - INTERVAL '2 days', 1, 1, 1),  -- Validée
(4, 2, CURRENT_TIMESTAMP - INTERVAL '1 day', 2, 1, 2),  -- Validée
(5, 3, CURRENT_TIMESTAMP, 1, 2, 3),  -- Duplicata approuvé
(6, 3, CURRENT_TIMESTAMP, 2, 2, 4),  -- Duplicata approuvé
(7, 3, CURRENT_TIMESTAMP, 1, 2, 5),  -- Duplicata approuvé
(8, 3, CURRENT_TIMESTAMP, 2, 2, 6),  -- Duplicata approuvé
(9, 4, CURRENT_TIMESTAMP - INTERVAL '3 days', 1, 1, 1);  -- Scan terminé

-- PIÈCES PAR DEMANDE (uniquement les pièces qui existent)
INSERT INTO demande_piece (id, id_demande, id_piece_justificative) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3), (4, 1, 4), (5, 1, 5),
(6, 2, 1), (7, 2, 2), (8, 2, 3), (9, 2, 4), (10, 2, 5),
(11, 3, 1), (12, 3, 2), (13, 3, 3), (14, 3, 4), (15, 3, 5),
(16, 4, 1), (17, 4, 2), (18, 4, 3), (19, 4, 4), (20, 4, 5),
(21, 5, 1), (22, 5, 2), (23, 5, 3), (24, 5, 4), (25, 5, 5),
(26, 6, 1), (27, 6, 2), (28, 6, 3), (29, 6, 4), (30, 6, 5),
(31, 7, 1), (32, 7, 2), (33, 7, 3), (34, 7, 4), (35, 7, 5),
(36, 8, 1), (37, 8, 2), (38, 8, 3), (39, 8, 4), (40, 8, 5),
(41, 9, 1), (42, 9, 2), (43, 9, 3), (44, 9, 4), (45, 9, 5);

-- HISTORIQUE
INSERT INTO historique_statut_demande (id_demande, id_statut_demande, id_administrateur, date_update) VALUES
(1, 1, 1, CURRENT_TIMESTAMP),
(2, 1, 1, CURRENT_TIMESTAMP),
(3, 1, 1, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(3, 2, 1, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4, 1, 1, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4, 2, 1, CURRENT_TIMESTAMP),
(5, 3, 1, CURRENT_TIMESTAMP),
(6, 3, 1, CURRENT_TIMESTAMP),
(7, 3, 1, CURRENT_TIMESTAMP),
(8, 3, 1, CURRENT_TIMESTAMP),
(9, 1, 1, CURRENT_TIMESTAMP - INTERVAL '3 days'),
(9, 2, 1, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(9, 4, 1, CURRENT_TIMESTAMP - INTERVAL '1 day');

-- VÉRIFICATION
SELECT 
    d.id as demande_id,
    td.libelle as type_demande,
    tv.libelle as type_visa,
    sd.libelle as statut,
    CASE 
        WHEN sd.id = 1 THEN 'Bouton Valider visible'
        WHEN sd.id = 3 THEN 'Duplicata - Pas de bouton'
        WHEN sd.id = 2 THEN 'Déjà validée'
        WHEN sd.id = 4 THEN 'Scan terminé'
        ELSE 'Autre'
    END as comportement,
    COUNT(h.id) as nombre_historiques
FROM demande d
JOIN type_demande td ON d.id_type_demande = td.id
JOIN type_visa tv ON d.id_type_visa = tv.id
JOIN statut_demande sd ON d.id_statut = sd.id
LEFT JOIN historique_statut_demande h ON d.id = h.id_demande
GROUP BY d.id, td.libelle, tv.libelle, sd.libelle, sd.id
ORDER BY d.id;
