package com.example.visa.repository;

import com.example.visa.entities.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {
    
    Optional<Demandeur> findByNomAndPrenom(String nom, String prenom);
    
    @Query("SELECT d FROM Demandeur d WHERE LOWER(d.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND LOWER(d.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Demandeur> findByNomAndPrenomContainingIgnoreCase(@Param("nom") String nom, @Param("prenom") String prenom);
}
