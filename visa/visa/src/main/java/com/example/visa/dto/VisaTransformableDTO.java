package com.example.visa.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VisaTransformableDTO {
    private String numero;
    private LocalDate dateEntreeTerritoire;
    private String lieuEntreeTerritoire;
    private String numeroVisaTransformable;
    private LocalDate dateSortieTerritoire;
}
