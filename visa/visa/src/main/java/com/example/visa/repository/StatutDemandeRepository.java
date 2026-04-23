package com.example.visa.repository;

import com.example.visa.entities.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutDemandeRepository extends JpaRepository<StatutDemande, Integer> {
}
