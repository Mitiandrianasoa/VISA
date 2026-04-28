package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "demandeur")
public class Demandeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nationalite")
    private Nationalite idNationalite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situation_familiale")
    private SituationFamiliale idSituationFamiliale;

    @Column(name = "adresse_mada", nullable = false, length = Integer.MAX_VALUE)
    private String adresseMada;

    @Column(name = "contact", nullable = false, length = 50)
    private String contact;

    @Column(name = "email", length = 100)
    private String email;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "idDemandeur", fetch = FetchType.LAZY)
    private List<Passeport> passeports;

}