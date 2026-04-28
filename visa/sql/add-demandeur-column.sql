-- Ajouter la colonne id_demandeur dans la table demande
ALTER TABLE demande 
ADD COLUMN id_demandeur INTEGER;

-- Créer la contrainte de clé étrangère
ALTER TABLE demande 
ADD CONSTRAINT fk_demande_demandeur 
FOREIGN KEY (id_demandeur) REFERENCES demandeur(id);

-- Mettre à jour les demandes existantes avec le demandeur correspondant
UPDATE demande d 
SET id_demandeur = (
    SELECT vp.id_demandeur 
    FROM visa_transformable vt 
    JOIN passeport vp ON vt.id_passeport = vp.id 
    WHERE vt.id = d.id_visa_transformable
)
WHERE d.id_visa_transformable IS NOT NULL;

-- Pour les demandes sans visa_transformable, vous devrez les mettre à jour manuellement
-- ou utiliser une autre logique selon votre cas d'usage
