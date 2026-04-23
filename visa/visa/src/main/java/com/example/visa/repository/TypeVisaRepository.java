package com.example.visa.repository;

import com.example.visa.entities.TypeVisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeVisaRepository extends JpaRepository<TypeVisa, Integer> {
}
