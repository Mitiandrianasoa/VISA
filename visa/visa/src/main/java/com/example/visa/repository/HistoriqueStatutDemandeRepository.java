package com.example.visa.repository;

import com.example.visa.entities.HistoriqueStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Integer> {
    
    @Query("SELECT h FROM HistoriqueStatutDemande h WHERE h.idDemande.id = :demandeId ORDER BY h.dateUpdate DESC")
    List<HistoriqueStatutDemande> findByDemandeIdOrderByDateUpdateDesc(Integer demandeId);
    
    @Query("SELECT h FROM HistoriqueStatutDemande h ORDER BY h.dateUpdate DESC")
    List<HistoriqueStatutDemande> findAllOrderByDateUpdateDesc();
}
