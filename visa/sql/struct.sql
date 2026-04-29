-- Création des tables
create database visa;
\c visa;
CREATE TABLE situation_familiale (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandeur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    date_naissance DATE NOT NULL,
    id_nationalite INTEGER REFERENCES nationalite(id),
    id_situation_familiale INTEGER REFERENCES situation_familiale(id),
    adresse_mada TEXT NOT NULL,
    contact VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE passeport (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    id_demandeur INTEGER NOT NULL REFERENCES demandeur(id),
    date_delivrance DATE NOT NULL,
    date_expiration DATE NOT NULL
);

CREATE TABLE visa_transformable (
    id SERIAL PRIMARY KEY,
    id_passeport INTEGER NOT NULL REFERENCES passeport(id),
    numero VARCHAR(50) NOT NULL,
    date_entree_territoire DATE NOT NULL,
    lieu_entree_territoire VARCHAR(200) NOT NULL,
    numero_visa_transformable VARCHAR(50) NOT NULL,
    date_sortie_territoire DATE
);

CREATE TABLE type_visa (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE piece_justificative (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(200) NOT NULL,
    commun BOOLEAN DEFAULT FALSE,
    obligatoire BOOLEAN DEFAULT FALSE
);


CREATE TABLE piece_specifique_type_visa (
    id_type_visa INTEGER NOT NULL REFERENCES type_visa(id),
    id_piece_justificative INTEGER NOT NULL REFERENCES piece_justificative(id),
    PRIMARY KEY (id_type_visa, id_piece_justificative)
);

CREATE TABLE statut_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE demande (
    id SERIAL PRIMARY KEY,
    id_visa_transformable INTEGER REFERENCES visa_transformable(id),
    id_statut INTEGER NOT NULL REFERENCES statut_demande(id),
    date_demande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_type_visa INTEGER NOT NULL REFERENCES type_visa(id),
    id_type_demande INTEGER NOT NULL REFERENCES type_demande(id)
);

CREATE TABLE demande_piece (
    id_demande INTEGER NOT NULL REFERENCES demande(id),
    id_piece INTEGER NOT NULL REFERENCES piece_justificative(id),
    PRIMARY KEY (id_demande, id_piece)
);

CREATE TABLE administrateur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL
);

CREATE TABLE historique_statut_demande (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES demande(id),
    id_administrateur INTEGER NOT NULL REFERENCES administrateur(id),
    date_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_statut_demande INTEGER NOT NULL REFERENCES statut_demande(id)
);

CREATE TABLE visa (
    id SERIAL PRIMARY KEY,
    date_delivrance DATE NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    numero_visa VARCHAR(50) NOT NULL UNIQUE,
    id_demande INTEGER NOT NULL UNIQUE REFERENCES demande(id)
);

CREATE TABLE carte_resident (
    id SERIAL PRIMARY KEY,
    date_delivrance DATE NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    numero_carte_resident VARCHAR(50) NOT NULL UNIQUE,
    id_demande INTEGER NOT NULL UNIQUE REFERENCES demande(id)
);

-- Création des index pour optimiser les performances

CREATE INDEX idx_demandeur_nationalite ON demandeur(id_nationalite);
CREATE INDEX idx_demandeur_situation ON demandeur(id_situation_familiale);
CREATE INDEX idx_passeport_demandeur ON passeport(id_demandeur);
CREATE INDEX idx_visa_transformable_passeport ON visa_transformable(id_passeport);
CREATE INDEX idx_demande_statut ON demande(id_statut);
CREATE INDEX idx_demande_type_visa ON demande(id_type_visa);
CREATE INDEX idx_demande_type_demande ON demande(id_type_demande);
CREATE INDEX idx_demande_visa_transformable ON demande(id_visa_transformable);
CREATE INDEX idx_historique_demande ON historique_statut_demande(id_demande);
CREATE INDEX idx_historique_administrateur ON historique_statut_demande(id_administrateur);
CREATE INDEX idx_historique_statut ON historique_statut_demande(id_statut_demande);
CREATE INDEX idx_piece_specifique_type ON piece_specifique_type_visa(id_type_visa);
CREATE INDEX idx_demande_piece_demande ON demande_piece(id_demande);

-- Création des triggers pour mettre à jour automatiquement le statut dans demande

CREATE OR REPLACE FUNCTION update_demande_statut()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE demande 
    SET id_statut = NEW.id_statut_demande
    WHERE id = NEW.id_demande;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_demande_statut
AFTER INSERT ON historique_statut_demande
FOR EACH ROW
EXECUTE FUNCTION update_demande_statut();

-- Commentaires sur les tables

COMMENT ON TABLE demande IS 'Table principale des demandes de visa';
COMMENT ON COLUMN demande.id_visa_transformable IS 'Optionnel - requis uniquement pour les demandes de transformation';
COMMENT ON TABLE visa_transformable IS 'Visa existant pouvant être transformé';
COMMENT ON TABLE type_demande IS 'Types de demande: renouvellement, duplicata, transformation';
COMMENT ON TABLE piece_justificative IS 'Documents requis pour les demandes';

SELECT setval('passeport_id_seq', (SELECT MAX(id) FROM passeport));
SELECT setval('demandeur_id_seq', (SELECT MAX(id) FROM demandeur));
SELECT setval('demande_id_seq', (SELECT MAX(id) FROM demande));