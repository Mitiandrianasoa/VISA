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

import java.util.Optional;

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
    private final DocumentDemandeurRepository documentDemandeurRepository;
    private final QRCodeService qrCodeService;

    @Value("${upload.path:uploads/scans}")
    private String uploadPath;

    @Value("${pdf.output.path:pdf}")
    private String pdfOutputPath;

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

    @Transactional(readOnly = true)
    public ScanFile getScanFileById(Integer scanFileId) {
        return scanFileRepository.findById(scanFileId)
                .orElseThrow(() -> new IllegalArgumentException("Fichier non trouvé : " + scanFileId));
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

        // Vérifier que photo et signature sont enregistrées
        Demandeur demandeur = demande.getIdDemandeur();
        if (demandeur != null) {
            Optional<DocumentDemandeur> doc = documentDemandeurRepository.findByIdDemandeurId(demandeur.getId());
            boolean photoOk = doc.isPresent() && doc.get().getPhoto() != null && !doc.get().getPhoto().isBlank();
            boolean signatureOk = doc.isPresent() && doc.get().getSignature() != null && !doc.get().getSignature().isBlank();
            if (!photoOk || !signatureOk) {
                throw new IllegalStateException("La photo et la signature du demandeur sont requises avant de terminer le scan");
            }
        }

        // Changer le statut à "Scan terminé"
        StatutDemande scanTermine = statutDemandeRepository.findByCode("SCAN")
                .orElseThrow(() -> new IllegalArgumentException("Statut SCAN non trouvé"));

        // Utiliser la méthode centralisée pour créer l'historique
        demandeService.creerHistoriqueCentralise(demande, scanTermine.getId(), "scan terminé");

        System.out.println("HISTORIQUE CENTRALISÉ - scan terminé - Demande ID: " + demandeId + " - Statut: " + scanTermine.getLibelle());

        // Sauvegarder le PDF dans visa/visa/pdf/{demandeId}/
        try {
            byte[] pdfBytes = generatePDF(demandeId);
            Path pdfDir = Paths.get(pdfOutputPath, String.valueOf(demandeId));
            Files.createDirectories(pdfDir);
            Path pdfFile = pdfDir.resolve("recepice_demande_" + demandeId + ".pdf");
            Files.write(pdfFile, pdfBytes);
            System.out.println("PDF sauvegardé : " + pdfFile.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde PDF demande " + demandeId + " : " + e.getMessage());
        }
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

    @Transactional(readOnly = true)
    public byte[] generateBulkZip(List<Integer> demandeIds) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
            for (Integer demandeId : demandeIds) {
                String folder = demandeId + "/";

                // 1. Récépicé PDF généré
                try {
                    byte[] pdf = generatePDF(demandeId);
                    zos.putNextEntry(new java.util.zip.ZipEntry(folder + "recepice_demande_" + demandeId + ".pdf"));
                    zos.write(pdf);
                    zos.closeEntry();
                } catch (Exception e) {
                    byte[] note = ("Erreur génération PDF: " + e.getMessage()).getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    zos.putNextEntry(new java.util.zip.ZipEntry(folder + "erreur_pdf.txt"));
                    zos.write(note);
                    zos.closeEntry();
                }

                // 2. Fichiers scannés uploadés
                List<ScanFile> scanFiles = scanFileRepository.findByDemandeId(demandeId);
                java.util.Set<String> usedNames = new java.util.HashSet<>();
                for (ScanFile sf : scanFiles) {
                    Path filePath = Paths.get(sf.getCheminFichier());
                    if (Files.exists(filePath)) {
                        String name = sf.getNomFichier();
                        if (usedNames.contains(name)) {
                            name = sf.getId() + "_" + name;
                        }
                        usedNames.add(name);
                        zos.putNextEntry(new java.util.zip.ZipEntry(folder + "scans/" + name));
                        zos.write(Files.readAllBytes(filePath));
                        zos.closeEntry();
                    }
                }
            }
        }
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] generateAttestationPDF(Integer demandeId) throws Exception {
        java.util.Map<String, Object> data = demandeService.getAttestationData(demandeId);

        // QR code en base64
        String qrUrl = "http://localhost:5173/?numeroDemande=" + demandeId;
        byte[] qrBytes = qrCodeService.generateQRCode(qrUrl, 150, 150);
        String qrBase64 = "data:image/png;base64," +
                java.util.Base64.getEncoder().encodeToString(qrBytes);

        String html = buildAttestationHtml(data, qrBase64);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, out);
        return out.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private String buildAttestationHtml(java.util.Map<String, Object> data, String qrBase64) {
        java.util.Map<String, Object> dem  = (java.util.Map<String, Object>) data.getOrDefault("demande", java.util.Map.of());
        java.util.Map<String, Object> pers = (java.util.Map<String, Object>) data.getOrDefault("demandeur", java.util.Map.of());
        java.util.Map<String, Object> pass = (java.util.Map<String, Object>) data.getOrDefault("passeport", java.util.Map.of());
        java.util.Map<String, Object> visa = (java.util.Map<String, Object>) data.getOrDefault("visa", null);
        String photo = (String) data.getOrDefault("photo", "");
        String sig   = (String) data.getOrDefault("signature", "");

        String fmtDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // date formatter inline
        java.util.function.Function<Object, String> fd = v -> {
            if (v == null) return "—";
            try {
                return java.time.LocalDate.parse(v.toString().substring(0, 10))
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) { return v.toString(); }
        };

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<style>");
        sb.append("body{font-family:Arial,sans-serif;font-size:11px;color:#1e293b;margin:20px;}");
        sb.append("h1{font-size:14px;text-align:center;color:#1e3a5f;text-transform:uppercase;margin:0;}");
        sb.append("h2{font-size:11px;text-align:center;color:#475569;margin:2px 0 0;}");
        sb.append(".header{border-bottom:2px solid #1e3a5f;padding-bottom:10px;margin-bottom:12px;}");
        sb.append(".header-table{width:100%;border-collapse:collapse;}");
        sb.append(".ref{background:#1e3a5f;color:white;padding:3px 8px;font-size:10px;font-weight:bold;}");
        sb.append(".section{margin-bottom:10px;}");
        sb.append(".section-title{font-size:10px;font-weight:bold;color:#1e3a5f;text-transform:uppercase;");
        sb.append("border-bottom:1px solid #e2e8f0;padding-bottom:3px;margin-bottom:6px;}");
        sb.append(".info-table{width:100%;border-collapse:collapse;}");
        sb.append(".info-table td{padding:3px 5px;font-size:10px;}");
        sb.append(".label{font-weight:bold;color:#334155;width:160px;}");
        sb.append(".visa-band{background:#eff6ff;border-left:4px solid #1e3a5f;padding:8px 10px;margin-bottom:10px;}");
        sb.append(".visa-table{width:100%;border-collapse:collapse;}");
        sb.append(".visa-table td{text-align:center;padding:4px;}");
        sb.append(".v-label{font-size:9px;color:#64748b;}");
        sb.append(".v-value{font-size:12px;font-weight:bold;color:#1e3a5f;}");
        sb.append(".footer{font-size:9px;color:#94a3b8;text-align:center;border-top:1px solid #e2e8f0;padding-top:8px;margin-top:10px;}");
        sb.append("</style></head><body>");

        // En-tête
        sb.append("<div class='header'>");
        sb.append("<table class='header-table'><tr>");
        sb.append("<td style='width:33%;font-size:9px;color:#475569;'>RÉPUBLIQUE DE MADAGASCAR<br/>Ministère de l'Intérieur<br/>Direction de l'Immigration</td>");
        sb.append("<td style='width:34%;text-align:center;'>");
        sb.append("<h1>Attestation de Demande de Visa</h1>");
        sb.append("<h2>").append(sv(dem.get("typeVisa"))).append(" — ").append(sv(dem.get("typeDemande"))).append("</h2>");
        sb.append("</td>");
        sb.append("<td style='width:33%;text-align:right;font-size:9px;'>");
        sb.append("<span class='ref'>N° ").append(sv(dem.get("id"))).append("</span><br/>");
        sb.append("Émis le : ").append(fd.apply(dem.get("dateDemande"))).append("<br/>");
        sb.append("Statut : <b>").append(sv(dem.get("statut"))).append("</b>");
        sb.append("</td></tr></table></div>");

        // Corps : photo + infos côte à côte
        sb.append("<table style='width:100%;border-collapse:collapse;margin-bottom:10px;'><tr>");

        // Photo
        sb.append("<td style='width:140px;vertical-align:top;padding-right:15px;'>");
        if (photo != null && !photo.isEmpty()) {
            sb.append("<img src='").append(photo).append("' style='width:130px;height:160px;object-fit:cover;border:1px solid #1e3a5f;'/>");
        } else {
            sb.append("<div style='width:130px;height:160px;border:1px solid #cbd5e1;background:#f8fafc;display:flex;align-items:center;justify-content:center;font-size:9px;color:#94a3b8;text-align:center;'>Photo non<br/>disponible</div>");
        }
        sb.append("</td>");

        // Informations personnelles
        sb.append("<td style='vertical-align:top;'>");
        sb.append("<div class='section'><div class='section-title'>Informations du demandeur</div>");
        sb.append("<table class='info-table'>");
        row(sb, "Nom", sv(pers.get("nom")));
        row(sb, "Prénom", sv(pers.get("prenom")));
        row(sb, "Date de naissance", fd.apply(pers.get("dateNaissance")));
        row(sb, "Nationalité", sv(pers.get("nationalite")));
        row(sb, "Situation familiale", sv(pers.get("situationFamiliale")));
        row(sb, "Adresse Madagascar", sv(pers.get("adresseMada")));
        row(sb, "Contact", sv(pers.get("contact")));
        row(sb, "Email", sv(pers.get("email")));
        sb.append("</table></div>");
        sb.append("<div class='section'><div class='section-title'>Passeport</div>");
        sb.append("<table class='info-table'>");
        row(sb, "Numéro", sv(pass.get("numero")));
        row(sb, "Date délivrance", fd.apply(pass.get("dateDelivrance")));
        row(sb, "Date expiration", fd.apply(pass.get("dateExpiration")));
        sb.append("</table></div>");
        sb.append("</td></tr></table>");

        // Bande visa
        sb.append("<div class='visa-band'><div class='section-title'>Informations du visa</div>");
        sb.append("<table class='visa-table'><tr>");
        sb.append("<td><div class='v-label'>Type</div><div class='v-value'>").append(sv(dem.get("typeVisa"))).append("</div></td>");
        if (visa != null) {
            sb.append("<td><div class='v-label'>Numéro visa</div><div class='v-value'>").append(sv(visa.get("numeroVisa"))).append("</div></td>");
            sb.append("<td><div class='v-label'>Valable jusqu'au</div><div class='v-value'>").append(fd.apply(visa.get("dateFin"))).append("</div></td>");
        } else {
            sb.append("<td><div class='v-label'>Statut</div><div class='v-value'>").append(sv(dem.get("statut"))).append("</div></td>");
            sb.append("<td><div class='v-label'>Exp. passeport</div><div class='v-value'>").append(fd.apply(pass.get("dateExpiration"))).append("</div></td>");
        }
        sb.append("</tr></table></div>");

        // Signature + QR
        sb.append("<table style='width:100%;border-collapse:collapse;padding-top:10px;border-top:1px solid #e2e8f0;margin-top:10px;'><tr>");
        sb.append("<td style='vertical-align:bottom;'>");
        sb.append("<div style='font-size:9px;color:#64748b;margin-bottom:4px;'>Signature du demandeur</div>");
        if (sig != null && !sig.isEmpty()) {
            sb.append("<img src='").append(sig).append("' style='max-width:220px;max-height:60px;border-bottom:1px solid #334155;'/>");
        } else {
            sb.append("<div style='width:220px;border-bottom:1px solid #334155;height:50px;'></div>");
        }
        sb.append("</td>");
        sb.append("<td style='text-align:center;width:130px;vertical-align:bottom;'>");
        sb.append("<img src='").append(qrBase64).append("' style='width:100px;height:100px;'/>");
        sb.append("<div style='font-size:9px;color:#64748b;margin-top:3px;'>Scanner pour consulter<br/>le dossier en ligne</div>");
        sb.append("</td></tr></table>");

        sb.append("<div class='footer'>Document généré le ").append(fmtDate)
          .append(" — Système de Gestion des Demandes de Visa Madagascar — Ce document ne constitue pas un visa délivré.</div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private void row(StringBuilder sb, String label, String value) {
        sb.append("<tr><td class='label'>").append(label).append(" :</td>")
          .append("<td>").append(value).append("</td></tr>");
    }

    private String sv(Object v) {
        return v != null ? escapeHtml(v.toString()) : "—";
    }
}
