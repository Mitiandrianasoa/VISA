-- ========================================
-- VIDAGE COMPLET DE TOUTES LES TABLES
-- ========================================

-- Desactiver temporairement les contraintes de clés etrangeres
SET session_replication_role = replica;

-- Vider toutes les tables dans le bon ordre (tables filles en premier)
TRUNCATE TABLE historique_statut_demande CASCADE;
TRUNCATE TABLE demande_piece CASCADE;
TRUNCATE TABLE scan_file CASCADE;
TRUNCATE TABLE demande CASCADE;
TRUNCATE TABLE passeport CASCADE;
TRUNCATE TABLE demandeur CASCADE;
TRUNCATE TABLE piece_specifique_type_visa CASCADE;
TRUNCATE TABLE piece_justificative CASCADE;
TRUNCATE TABLE administrateur CASCADE;
TRUNCATE TABLE type_visa CASCADE;
TRUNCATE TABLE type_demande CASCADE;
TRUNCATE TABLE statut_demande CASCADE;
TRUNCATE TABLE nationalite CASCADE;
TRUNCATE TABLE situation_familiale CASCADE;
TRUNCATE TABLE visa_transformable CASCADE;

-- Reinitialiser toutes les sequences
ALTER SEQUENCE IF EXISTS administrateur_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS demandeur_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS passeport_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS demande_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS demande_piece_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS historique_statut_demande_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS nationalite_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS situation_familiale_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS statut_demande_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS type_demande_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS type_visa_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS piece_justificative_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS piece_specifique_type_visa_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS scan_file_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS visa_transformable_id_seq RESTART WITH 1;

-- Reactiver les contraintes de clés etrangeres
SET session_replication_role = DEFAULT;

-- Verification que toutes les tables sont bien vides
SELECT 
    'historique_statut_demande' as table_name, COUNT(*) as row_count FROM historique_statut_demande
UNION ALL
SELECT 'demande_piece' as table_name, COUNT(*) as row_count FROM demande_piece
UNION ALL
SELECT 'scan_file' as table_name, COUNT(*) as row_count FROM scan_file
UNION ALL
SELECT 'demande' as table_name, COUNT(*) as row_count FROM demande
UNION ALL
SELECT 'passeport' as table_name, COUNT(*) as row_count FROM passeport
UNION ALL
SELECT 'demandeur' as table_name, COUNT(*) as row_count FROM demandeur
UNION ALL
SELECT 'piece_specifique_type_visa' as table_name, COUNT(*) as row_count FROM piece_specifique_type_visa
UNION ALL
SELECT 'piece_justificative' as table_name, COUNT(*) as row_count FROM piece_justificative
UNION ALL
SELECT 'administrateur' as table_name, COUNT(*) as row_count FROM administrateur
UNION ALL
SELECT 'type_visa' as table_name, COUNT(*) as row_count FROM type_visa
UNION ALL
SELECT 'type_demande' as table_name, COUNT(*) as row_count FROM type_demande
UNION ALL
SELECT 'statut_demande' as table_name, COUNT(*) as row_count FROM statut_demande
UNION ALL
SELECT 'nationalite' as table_name, COUNT(*) as row_count FROM nationalite
UNION ALL
SELECT 'situation_familiale' as table_name, COUNT(*) as row_count FROM situation_familiale
UNION ALL
SELECT 'visa_transformable' as table_name, COUNT(*) as row_count FROM visa_transformable
ORDER BY table_name;