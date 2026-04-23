package com.example.visa.repository;

import com.example.visa.entities.DemandePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandePieceRepository extends JpaRepository<DemandePiece, Integer> {
}
