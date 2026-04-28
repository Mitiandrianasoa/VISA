package com.example.visa.repository;

import com.example.visa.entities.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
    
    List<Passeport> findByIdDemandeurId(Integer demandeurId);
}
