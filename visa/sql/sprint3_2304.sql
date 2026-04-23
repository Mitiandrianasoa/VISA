

-- ============================================
-- 1. CRÉER LA TABLE SCAN_FILE
-- ============================================
-- Créer la table scan_file si elle n'existe pas
CREATE TABLE IF NOT EXISTS scan_file (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER NOT NULL REFERENCES demande(id) ON DELETE CASCADE,
    nom_fichier VARCHAR(255) NOT NULL,
    chemin_fichier VARCHAR(500) NOT NULL,
    type_fichier VARCHAR(50),
    date_upload TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    taille_fichier BIGINT
);

COMMENT ON TABLE scan_file IS 'Stocke les fichiers scannés uploadés pour chaque demande de visa';
COMMENT ON COLUMN scan_file.id_demande IS 'Référence à la demande associée';
COMMENT ON COLUMN scan_file.nom_fichier IS 'Nom original du fichier uploadé';
COMMENT ON COLUMN scan_file.chemin_fichier IS 'Chemin physique du fichier sur le serveur';
COMMENT ON COLUMN scan_file.type_fichier IS 'Type MIME du fichier (ex: application/pdf, image/png)';
COMMENT ON COLUMN scan_file.date_upload IS 'Date et heure de l\'upload';
COMMENT ON COLUMN scan_file.taille_fichier IS 'Taille du fichier en bytes';

-- ============================================
-- 2. CRÉER LES INDEX POUR PERFORMANCE
-- ============================================
-- Index pour accélérer les requêtes par demande
CREATE INDEX IF NOT EXISTS idx_scan_file_demande ON scan_file(id_demande);

-- ============================================
-- 3. AJOUTER LE STATUT "SCAN_TERMINÉ"
-- ============================================
-- Vérifier et ajouter le statut si nécessaire (avec gestion des doublons)
INSERT INTO statut_demande (libelle, code)
SELECT 'Scan terminé', 'SCAN_TERMINE'
WHERE NOT EXISTS (
    SELECT 1 FROM statut_demande 
    WHERE code = 'SCAN_TERMINE'
);

-- ============================================
-- 4. VÉRIFICATION DES STATUTS
-- ============================================
-- Afficher tous les statuts créés
SELECT 'Statuts disponibles:' as info;
SELECT id, libelle, code FROM statut_demande ORDER BY id;

-- ============================================
-- 5. VÉRIFICATION DES TABLES
-- ============================================
-- Vérifier la structure de la table scan_file
SELECT 'Structure de la table scan_file:' as info;
SELECT * FROM information_schema.columns WHERE table_name = 'scan_file' ORDER BY ordinal_position;
