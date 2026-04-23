package com.example.visa.repository;

import com.example.visa.entities.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Integer> {
}
