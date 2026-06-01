-- Migration Sprint 5 - Photo et Signature
-- À exécuter sur une base de données existante

-- Création de la table document_demandeur (si elle n'existe pas déjà)
CREATE TABLE IF NOT EXISTS document_demandeur (
    id SERIAL PRIMARY KEY,
    id_demandeur INTEGER NOT NULL REFERENCES demandeur(id) ON DELETE CASCADE,
    photo TEXT,
    signature TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Si la table existait déjà avec VARCHAR(255), migrer vers TEXT
ALTER TABLE document_demandeur ALTER COLUMN photo TYPE TEXT;
ALTER TABLE document_demandeur ALTER COLUMN signature TYPE TEXT;
