package com.example.visa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScanFileDTO {
    private Integer id;
    private Integer idDemande;
    private String nomFichier;
    private String cheminFichier;
    private String typeFichier;
    private Long tailleFichier;
    private String dateUpload;
}
