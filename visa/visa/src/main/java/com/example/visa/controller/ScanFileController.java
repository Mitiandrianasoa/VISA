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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
}
