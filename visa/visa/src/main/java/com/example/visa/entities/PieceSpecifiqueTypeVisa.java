package com.example.visa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "piece_specifique_type_visa")
public class PieceSpecifiqueTypeVisa {
    @EmbeddedId
    private PieceSpecifiqueTypeVisaId id;

    @MapsId("idTypeVisa")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type_visa", nullable = false)
    private TypeVisa idTypeVisa;

    @MapsId("idPieceJustificative")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_piece_justificative", nullable = false)
    private PieceJustificative idPieceJustificative;

}