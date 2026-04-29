package com.example.visa.repository;

import com.example.visa.dto.api.DemandeListeItemDTO;
import com.example.visa.entities.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeListeApiRepository extends JpaRepository<Demande, Long> {

    @Query("""
        SELECT new com.example.visa.dto.api.DemandeListeItemDTO(
            d.id,
            CAST(d.id as string),
            d.dateDemande,
            sd.libelle,
            p.numero
        )
        FROM Demande d
        LEFT JOIN d.idVisaTransformable vt
        LEFT JOIN d.idDemandeur dr
        LEFT JOIN dr.passeports p
        JOIN d.idStatut sd
        WHERE (
            :numeroPasseport IS NULL OR p.numero = :numeroPasseport
        ) OR (
            :numeroDemande IS NULL OR CAST(d.id as string) = :numeroDemande
        )
        ORDER BY
            CASE WHEN CAST(d.id as string) = :numeroDemande THEN 0 ELSE 1 END,
            d.dateDemande DESC
    """)
    List<DemandeListeItemDTO> rechercherPourApi(
            @Param("numeroDemande") String numeroDemande,
            @Param("numeroPasseport") String numeroPasseport
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
        FROM HistoriqueStatutDemande hs
        JOIN hs.idDemande d
        JOIN d.idDemandeur dem
        LEFT JOIN dem.passeports p
        LEFT JOIN d.idTypeVisa tv
        JOIN hs.idStatutDemande st
        WHERE dem.id = (
            SELECT dRef.idDemandeur.id
            FROM Demande dRef
            WHERE dRef.id = :numeroDemande
        )
        ORDER BY
            CASE WHEN d.id = :numeroDemande THEN 0 ELSE 1 END,
            hs.dateUpdate ASC,
            hs.id ASC
    """)
    List<DemandeListeItemDTO> rechercherParNumeroDemande(@Param("numeroDemande") Integer numeroDemande);

}
