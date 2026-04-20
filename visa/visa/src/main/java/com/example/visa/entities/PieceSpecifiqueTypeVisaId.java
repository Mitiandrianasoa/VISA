package com.example.visa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PieceSpecifiqueTypeVisaId implements Serializable {
    private static final long serialVersionUID = -1331666630676476099L;
    @Column(name = "id_type_visa", nullable = false)
    private Integer idTypeVisa;

    @Column(name = "id_piece_justificative", nullable = false)
    private Integer idPieceJustificative;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PieceSpecifiqueTypeVisaId entity = (PieceSpecifiqueTypeVisaId) o;
        return Objects.equals(this.idTypeVisa, entity.idTypeVisa) &&
                Objects.equals(this.idPieceJustificative, entity.idPieceJustificative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTypeVisa, idPieceJustificative);
    }

}