package com.example.visa.repository;

import com.example.visa.entities.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Integer> {
    
    @Query("SELECT d FROM Demande d JOIN d.idVisaTransformable vt JOIN vt.idPasseport p WHERE p.idDemandeur.id = :demandeurId")
    List<Demande> findByDemandeurId(@Param("demandeurId") Integer demandeurId);
}
