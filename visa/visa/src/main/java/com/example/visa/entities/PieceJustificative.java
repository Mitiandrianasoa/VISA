package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "piece_justificative")
public class PieceJustificative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @ColumnDefault("false")
    @Column(name = "commun")
    private Boolean commun;

    @ColumnDefault("false")
    @Column(name = "obligatoire")
    private Boolean obligatoire;

}