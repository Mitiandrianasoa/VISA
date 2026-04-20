package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "demande_piece")
public class DemandePiece {
    @EmbeddedId
    private DemandePieceId id;

    @MapsId("idDemande")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_demande", nullable = false)
    private Demande idDemande;

    @MapsId("idPiece")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_piece", nullable = false)
    private PieceJustificative idPiece;

}