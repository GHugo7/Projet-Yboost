package com.example.skillforge.controller;

import com.example.skillforge.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    // ─── Export CSV ───────────────────────────────────────────
    // Appelé par : /export/csv ou /export/csv?category=Backend
    // Content-Disposition: attachment → force le téléchargement
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String category) {

        byte[] data = exportService.exportCsv(category);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"skillforge.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(data);
    }

    // ─── Export JSON ──────────────────────────────────────────
    // Appelé par : /export/json ou /export/json?category=Backend
    @GetMapping("/json")
    public ResponseEntity<byte[]> exportJson(
            @RequestParam(required = false) String category) {

        byte[] data = exportService.exportJson(category);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"skillforge.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(data);
    }

    // ─── Export PDF ───────────────────────────────────────────
    // Appelé par : /export/pdf ou /export/pdf?category=Backend
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String category) throws IOException {

        byte[] data = exportService.exportPdf(category);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"skillforge.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}