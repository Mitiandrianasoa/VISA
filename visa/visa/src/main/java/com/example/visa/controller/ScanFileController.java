package com.example.visa.controller;

import com.example.visa.dto.ScanFileDTO;
import com.example.visa.service.ScanFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.visa.entities.ScanFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScanFileController {

    private final ScanFileService scanFileService;

    @PostMapping("/{demandeId}/scan/upload")
    public ResponseEntity<?> uploadScanFile(@PathVariable Integer demandeId,
            @RequestParam("file") MultipartFile file) {
        try {
            ScanFileDTO scanFile = scanFileService.uploadFile(demandeId, file);
            return ResponseEntity.ok(new Object() {
                public boolean success = true;
                public String message = "Fichier uploadé avec succès";
                public ScanFileDTO data = scanFile;
            });
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = "Erreur lors de l'upload: " + e.getMessage();
            });
        }
    }

    @GetMapping("/{demandeId}/scan/files")
    public ResponseEntity<?> getScanFiles(@PathVariable Integer demandeId) {
        try {
            List<ScanFileDTO> files = scanFileService.getScanFilesByDemandeId(demandeId);
            return ResponseEntity.ok(new Object() {
                public boolean success = true;
                public List<ScanFileDTO> data = files;
                public int count = files.size();
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        }
    }

    @DeleteMapping("/scan/{scanFileId}")
    public ResponseEntity<?> deleteScanFile(@PathVariable Integer scanFileId) {
        try {
            scanFileService.deleteScanFile(scanFileId);
            return ResponseEntity.ok(new Object() {
                public boolean success = true;
                public String message = "Fichier supprimé avec succès";
            });
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = "Erreur lors de la suppression: " + e.getMessage();
            });
        }
    }

    @PostMapping("/{demandeId}/scan/completer")
    public ResponseEntity<?> completerScan(@PathVariable Integer demandeId) {
        try {
            scanFileService.completerScan(demandeId);
            return ResponseEntity.ok(new Object() {
                public boolean success = true;
                public String message = "Scan complété avec succès. Le dossier ne peut plus être modifié.";
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        }
    }

    @GetMapping("/{demandeId}/pdf")
    public ResponseEntity<?> generatePDF(@PathVariable Integer demandeId) {
        try {
            byte[] pdfContent = scanFileService.generatePDF(demandeId);

            String fileName = "recepice_demande_" + demandeId + "_" + LocalDate.now() + ".pdf";
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = "Erreur lors de la génération du PDF: " + e.getMessage();
            });
        }
    }

    @GetMapping("/scan/{scanFileId}/view")
    public ResponseEntity<?> viewScanFile(@PathVariable Integer scanFileId) {
        try {
            ScanFile sf = scanFileService.getScanFileById(scanFileId);
            Path filePath = Paths.get(sf.getCheminFichier());
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            byte[] content = Files.readAllBytes(filePath);
            String contentType = sf.getTypeFichier() != null ? sf.getTypeFichier() : "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sf.getNomFichier() + "\"")
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = e.getMessage();
            });
        }
    }

    @PostMapping("/bulk-zip")
    public ResponseEntity<?> generateBulkZip(@RequestBody Map<String, List<Integer>> payload) {
        try {
            List<Integer> ids = payload.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(new Object() {
                    public boolean success = false;
                    public String message = "Aucune demande sélectionnée";
                });
            }
            byte[] zipContent = scanFileService.generateBulkZip(ids);
            String fileName = "demandes_" + LocalDate.now() + ".zip";
            ContentDisposition cd = ContentDisposition.attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(zipContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = "Erreur: " + e.getMessage();
            });
        }
    }

    @GetMapping("/{demandeId}/attestation-pdf")
    public ResponseEntity<?> generateAttestationPDF(@PathVariable Integer demandeId) {
        try {
            byte[] pdfContent = scanFileService.generateAttestationPDF(demandeId);

            String fileName = "attestation_demande_" + demandeId + "_" + LocalDate.now() + ".pdf";
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public boolean success = false;
                public String message = "Erreur lors de la génération de l'attestation PDF: " + e.getMessage();
            });
        }
    }
}
