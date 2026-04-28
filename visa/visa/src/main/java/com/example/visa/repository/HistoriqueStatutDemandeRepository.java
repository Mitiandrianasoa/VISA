package com.example.visa.repository;

import com.example.visa.entities.HistoriqueStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Integer> {
    
    List<HistoriqueStatutDemande> findByIdDemandeIdOrderByDateUpdateDesc(Integer demandeId);
    
    List<HistoriqueStatutDemande> findByIdDemandeId(Integer demandeId);
    
    List<HistoriqueStatutDemande> findAllByOrderByDateUpdateDesc();
}
