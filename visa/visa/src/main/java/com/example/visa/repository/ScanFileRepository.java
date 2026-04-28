package com.example.visa.repository;

import com.example.visa.entities.ScanFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanFileRepository extends JpaRepository<ScanFile, Integer> {
    List<ScanFile> findByDemandeId(Integer demandeId);

    long countByDemandeId(Integer demandeId);
}
