package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "demande_piece")
@IdClass(DemandePieceId.class)
public class DemandePiece {
    @Id
    @Column(name = "id_demande", nullable = false)
    private Integer idDemande;

    @Id
    @Column(name = "id_piece", nullable = false)
    private Integer idPiece;

    @MapsId("idDemande")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_demande", nullable = false, insertable = false, updatable = false)
    private Demande demande;

    @MapsId("idPiece")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_piece", nullable = false, insertable = false, updatable = false)
    private PieceJustificative piece;

}