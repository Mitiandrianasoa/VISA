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
public class DemandePieceId implements Serializable {
    private static final long serialVersionUID = -5809559822733415475L;
    @Column(name = "id_demande", nullable = false)
    private Integer idDemande;

    @Column(name = "id_piece", nullable = false)
    private Integer idPiece;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DemandePieceId entity = (DemandePieceId) o;
        return Objects.equals(this.idPiece, entity.idPiece) &&
                Objects.equals(this.idDemande, entity.idDemande);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPiece, idDemande);
    }

}