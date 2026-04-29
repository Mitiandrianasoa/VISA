package com.example.visa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.visa.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;


    @GetMapping(value = "/generate-demande", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCodeForDemande(
            @RequestParam String numeroDemande,
            @RequestParam String numeroPasseport,
            @RequestParam(defaultValue = "300") int size) {

        try {
            // Construire l'URL de l'application Vue avec les paramètres
            String vueAppUrl = String.format(
                    "http://localhost:5173/?numeroDemande=%s",
                    numeroDemande
            );

            byte[] qrCodeImage = qrCodeService.generateQRCode(vueAppUrl, size, size);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Génère un QR code qui pointe vers l'URL fournie
     * @param url L'URL à encoder (ex: https://monsite.com/page/123)
     * @param size Taille du QR code (optionnel, défaut: 300)
     */
    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam String url,
            @RequestParam(defaultValue = "300") int size) {

        try {
            // ZXing va encoder cette URL exactement comme elle est
            byte[] qrCodeImage = qrCodeService.generateQRCode(url, size, size);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}