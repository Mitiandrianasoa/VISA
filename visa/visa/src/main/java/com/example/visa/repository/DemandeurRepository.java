package com.example.visa.repository;

import com.example.visa.entities.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
    
    Optional<Demandeur> findByNomAndPrenom(String nom, String prenom);
    
    List<Demandeur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
