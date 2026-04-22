package com.example.visa.repository;

import com.example.visa.entities.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Integer> {
    
    @Query("SELECT pj FROM PieceJustificative pj " +
           "WHERE pj.commun = true OR " +
           "EXISTS (SELECT 1 FROM PieceSpecifiqueTypeVisa pst WHERE pst.id.idTypeVisa = :typeVisaId AND pst.id.idPieceJustificative = pj.id)")
    List<PieceJustificative> findPiecesByTypeVisa(@Param("typeVisaId") Integer typeVisaId);
}
