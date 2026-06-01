package com.example.visa.repository;

import com.example.visa.entities.Visa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisaRepository extends JpaRepository<Visa, Integer> {
    Optional<Visa> findByIdDemandeId(Integer demandeId);
}
