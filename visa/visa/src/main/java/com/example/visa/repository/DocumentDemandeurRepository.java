package com.example.visa.repository;

import com.example.visa.entities.DocumentDemandeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentDemandeurRepository extends JpaRepository<DocumentDemandeur, Integer> {
    Optional<DocumentDemandeur> findByIdDemandeurId(Integer demandeurId);
}
