package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "visa_transformable")
public class VisaTransformable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_passeport", nullable = false)
    private Passeport idPasseport;

    @Column(name = "numero", nullable = false, length = 50)
    private String numero;

    @Column(name = "date_entree_territoire", nullable = false)
    private LocalDate dateEntreeTerritoire;

    @Column(name = "lieu_entree_territoire", nullable = false, length = 200)
    private String lieuEntreeTerritoire;

    @Column(name = "numero_visa_transformable", nullable = false, length = 50)
    private String numeroVisaTransformable;

    @Column(name = "date_sortie_territoire")
    private LocalDate dateSortieTerritoire;

}