package com.example.visa.repository;

import com.example.visa.entities.VisaTransformable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Integer> {
}
