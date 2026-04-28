package com.example.visa.repository;

import com.example.visa.dto.api.DemandeListeItemDTO;
import com.example.visa.entities.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeListeApiRepository extends JpaRepository<Demande, Integer> {

    @Query("""
        SELECT new com.example.visa.dto.api.DemandeListeItemDTO(
            d.id,
            p.numero,
            hs.dateUpdate,
            dem.nom,
            dem.prenom,
            tv.libelle,
            st.libelle
        )
        FROM Demande d
        JOIN d.idVisaTransformable vt
        JOIN vt.idPasseport p
        LEFT JOIN p.idDemandeur dem
        LEFT JOIN d.idTypeVisa tv
        JOIN HistoriqueStatutDemande hs ON hs.idDemande = d
        JOIN hs.idStatutDemande st
        WHERE LOWER(p.numero) LIKE LOWER(CONCAT('%', :numeroPasseport, '%'))
        ORDER BY
            CASE
                WHEN :numeroDemande IS NOT NULL AND d.id = :numeroDemande THEN 0
                ELSE 1
            END,
            hs.dateUpdate ASC,
            hs.id ASC
    """)
    List<DemandeListeItemDTO> rechercherPourApi(
            @Param("numeroPasseport") String numeroPasseport,
            @Param("numeroDemande") Integer numeroDemande
    );

    @Query("""
        SELECT new com.example.visa.dto.api.DemandeListeItemDTO(
            d.id,
            p.numero,
            hs.dateUpdate,
            dem.nom,
            dem.prenom,
            tv.libelle,
            st.libelle
        )
        FROM Demande d
        JOIN d.idVisaTransformable vt
        JOIN vt.idPasseport p
        LEFT JOIN p.idDemandeur dem
        LEFT JOIN d.idTypeVisa tv
        JOIN HistoriqueStatutDemande hs ON hs.idDemande = d
        JOIN hs.idStatutDemande st
        WHERE p.numero = (
            SELECT pRef.numero
            FROM Demande dRef
            JOIN dRef.idVisaTransformable vtRef
            JOIN vtRef.idPasseport pRef
            WHERE dRef.id = :numeroDemande
        )
        ORDER BY
            CASE WHEN d.id = :numeroDemande THEN 0 ELSE 1 END,
            hs.dateUpdate ASC,
            hs.id ASC
    """)
    List<DemandeListeItemDTO> rechercherParNumeroDemande(@Param("numeroDemande") Integer numeroDemande);

}
