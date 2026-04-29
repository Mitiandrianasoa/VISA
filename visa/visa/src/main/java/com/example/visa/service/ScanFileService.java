package com.example.visa.service;

import com.example.visa.dto.ScanFileDTO;
import com.example.visa.entities.*;
import com.example.visa.repository.*;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScanFileService {

    private final ScanFileRepository scanFileRepository;
    private final DemandeRepository demandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final DemandeService demandeService;

    @Value("${upload.path:uploads/scans}")
    private String uploadPath;

    public ScanFileDTO uploadFile(Integer demandeId, MultipartFile file) throws IOException {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        // Vérifier que la demande n'est pas "Scan terminé"
        if (demande.getIdStatut().getCode().equals("SCAN_TERMINE")) {
            throw new IllegalStateException("Cette demande ne peut plus être modifiée");
        }

        // Créer le dossier si nécessaire
        Path uploadDir = Paths.get(uploadPath, "demande_" + demandeId);
        Files.createDirectories(uploadDir);

        // Sauvegarder le fichier
        String fileName = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path filePath = uploadDir.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        // Créer l'enregistrement en base
        ScanFile scanFile = new ScanFile();
        scanFile.setDemande(demande);
        scanFile.setNomFichier(fileName);
        scanFile.setCheminFichier(filePath.toString());
        scanFile.setTypeFichier(file.getContentType());
        scanFile.setTailleFichier(file.getSize());
        scanFile.setDateUpload(Instant.now());

        scanFile = scanFileRepository.save(scanFile);

        return convertToDTO(scanFile);
    }

    public List<ScanFileDTO> getScanFilesByDemandeId(Integer demandeId) {
        return scanFileRepository.findByDemandeId(demandeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteScanFile(Integer scanFileId) throws IOException {
        ScanFile scanFile = scanFileRepository.findById(scanFileId)
                .orElseThrow(() -> new IllegalArgumentException("Fichier non trouvé"));

        Demande demande = scanFile.getDemande();
        if (demande.getIdStatut().getCode().equals("SCAN_TERMINE")) {
            throw new IllegalStateException("Cette demande ne peut plus être modifiée");
        }

        // Supprimer le fichier physique
        Path filePath = Paths.get(scanFile.getCheminFichier());
        Files.deleteIfExists(filePath);

        // Supprimer l'enregistrement
        scanFileRepository.deleteById(scanFileId);
    }

    @Transactional
    public void completerScan(Integer demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

        long scanCount = scanFileRepository.countByDemandeId(demandeId);
        if (scanCount == 0) {
            throw new IllegalStateException("Aucun fichier uploadé");
        }

        // Changer le statut à "Scan terminé"
        StatutDemande scanTermine = statutDemandeRepository.findByCode("SCAN")
                .orElseThrow(() -> new IllegalArgumentException("Statut SCAN non trouvé"));

        // Utiliser la méthode centralisée pour créer l'historique
        demandeService.creerHistoriqueCentralise(demande, scanTermine.getId(), "scan terminé");
        
        System.out.println("HISTORIQUE CENTRALISÉ - scan terminé - Demande ID: " + demandeId + " - Statut: " + scanTermine.getLibelle());
    }

    public byte[] generatePDF(Integer demandeId) throws IOException {
        try {
            Demande demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

            // Récupérer le demandeur
            Demandeur demandeur = null;
            if (demande.getIdVisaTransformable() != null &&
                    demande.getIdVisaTransformable().getIdPasseport() != null) {
                demandeur = demande.getIdVisaTransformable().getIdPasseport().getIdDemandeur();
            }

            String htmlContent = generateHTMLContent(demande, demandeur);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Utiliser HtmlConverter avec configuration UTF-8
            HtmlConverter.convertToPdf(htmlContent, outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private String generateHTMLContent(Demande demande, Demandeur demandeur) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='fr'>\n");
        html.append("<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("<title>Récépicé de Demande de Visa</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; color: #1e293b; }\n");
        html.append("h1 { color: #1e40af; text-align: center; }\n");
        html.append("h2 { color: #3730a3; border-bottom: 2px solid #e2e8f0; padding-bottom: 5px; }\n");
        html.append(".section { margin: 20px 0; padding: 15px; border: 1px solid #e2e8f0; border-radius: 8px; }\n");
        html.append(".info-row { margin: 12px 0; display: flex; }\n");
        html.append(".label { font-weight: bold; min-width: 200px; color: #334155; }\n");
        html.append(".value { flex: 1; color: #475569; }\n");
        html.append(
                ".footer { margin-top: 40px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; padding-top: 20px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("<h1>Récépicé de Demande de Visa Madagascar</h1>\n");
        html.append("<hr/>\n");

        // Section Demande
        html.append("<div class='section'>\n");
        html.append("<h2>Informations de la Demande</h2>\n");
        html.append("<div class='info-row'><span class='label'>ID Demande:</span><span class='value'>")
                .append(demande.getId()).append("</span></div>\n");
        html.append("<div class='info-row'><span class='label'>Type de Visa:</span><span class='value'>")
                .append(demande.getIdTypeVisa() != null ? escapeHtml(demande.getIdTypeVisa().getLibelle()) : "N/A")
                .append("</span></div>\n");
        html.append("<div class='info-row'><span class='label'>Type de Demande:</span><span class='value'>")
                .append(demande.getIdTypeDemande() != null ? escapeHtml(demande.getIdTypeDemande().getLibelle())
                        : "N/A")
                .append("</span></div>\n");
        html.append("<div class='info-row'><span class='label'>Date Demande:</span><span class='value'>")
                .append(formatDate(demande.getDateDemande()))
                .append("</span></div>\n");
        html.append("<div class='info-row'><span class='label'>Statut:</span><span class='value'>")
                .append(demande.getIdStatut() != null ? escapeHtml(demande.getIdStatut().getLibelle()) : "N/A")
                .append("</span></div>\n");
        html.append("</div>\n");

        // Section Demandeur
        if (demandeur != null) {
            html.append("<div class='section'>\n");
            html.append("<h2>Informations du Demandeur</h2>\n");
            html.append("<div class='info-row'><span class='label'>Nom:</span><span class='value'>")
                    .append(escapeHtml(demandeur.getNom())).append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Prénom:</span><span class='value'>")
                    .append(escapeHtml(demandeur.getPrenom())).append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Date de Naissance:</span><span class='value'>")
                    .append(demandeur.getDateNaissance()).append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Nationalité:</span><span class='value'>")
                    .append(demandeur.getIdNationalite() != null ? escapeHtml(demandeur.getIdNationalite().getLibelle())
                            : "N/A")
                    .append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Situation Familiale:</span><span class='value'>")
                    .append(demandeur.getIdSituationFamiliale() != null
                            ? escapeHtml(demandeur.getIdSituationFamiliale().getLibelle())
                            : "N/A")
                    .append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Adresse Madagascar:</span><span class='value'>")
                    .append(escapeHtml(demandeur.getAdresseMada())).append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Contact:</span><span class='value'>")
                    .append(escapeHtml(demandeur.getContact())).append("</span></div>\n");
            html.append("<div class='info-row'><span class='label'>Email:</span><span class='value'>")
                    .append(escapeHtml(demandeur.getEmail())).append("</span></div>\n");
            html.append("</div>\n");
        }

        html.append("<div class='footer'>\n");
        html.append("Généré le ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" - Système de Gestion des Demandes de Visa Madagascar");
        html.append("</div>\n");

        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "N/A";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String formatDate(Instant instant) {
        if (instant == null)
            return "N/A";
        return instant.atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }

    private ScanFileDTO convertToDTO(ScanFile scanFile) {
        ScanFileDTO dto = new ScanFileDTO();
        dto.setId(scanFile.getId());
        dto.setIdDemande(scanFile.getDemande().getId());
        dto.setNomFichier(scanFile.getNomFichier());
        dto.setCheminFichier(scanFile.getCheminFichier());
        dto.setTypeFichier(scanFile.getTypeFichier());
        dto.setTailleFichier(scanFile.getTailleFichier());
        dto.setDateUpload(scanFile.getDateUpload().toString());
        return dto;
    }
}
