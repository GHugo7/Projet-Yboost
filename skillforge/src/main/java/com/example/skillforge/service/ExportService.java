package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    private final SkillService skillService;

    public ExportService(SkillService skillService) {
        this.skillService = skillService;
    }

    // ─── Filtrage optionnel par catégorie ─────────────────────────────────────
    // Si category est null ou vide, on retourne tous les skills
    // Sinon on filtre sur la catégorie demandée
    private List<Skill> getSkills(String category) {
        List<Skill> all = skillService.getAll();
        if (category == null || category.isBlank()) return all;
        return all.stream()
                .filter(s -> category.equalsIgnoreCase(s.getCategory()))
                .collect(Collectors.toList());
    }

    // ─── Export CSV ───────────────────────────────────────────────────────────
    // Format : une ligne d'en-tête + une ligne par skill, séparées par des virgules
    public byte[] exportCsv(String category) {
        List<Skill> skills = getSkills(category);
        StringBuilder sb = new StringBuilder();

        // En-tête
        sb.append("Nom;Categorie;Niveau\n");

        // Une ligne par skill
        for (Skill s : skills) {
            sb.append(escapeCsv(s.getName())).append(";");
            sb.append(escapeCsv(s.getCategory())).append(";");
            sb.append(getLevelString(s.getLevel())).append("\n");
        }

        return sb.toString().getBytes();
    }

    // Échappe les virgules et guillemets dans les valeurs CSV
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ─── Export JSON ──────────────────────────────────────────────────────────
    // Format : tableau JSON d'objets { nom, categorie, niveau }
    public byte[] exportJson(String category) {
        List<Skill> skills = getSkills(category);
        StringBuilder sb = new StringBuilder();

        sb.append("[\n");
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            sb.append("  {\n");
            sb.append("    \"nom\": \"").append(escapeJson(s.getName())).append("\",\n");
            sb.append("    \"categorie\": \"").append(escapeJson(s.getCategory())).append("\",\n");
            sb.append("    \"niveau\": ").append(getLevelString(s.getLevel())).append("\n");
            sb.append("  }");
            if (i < skills.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");

        return sb.toString().getBytes();
    }

    // Échappe les caractères spéciaux dans les valeurs JSON
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ─── Export PDF ───────────────────────────────────────────────────────────
    // Génère un PDF avec un titre, un sous-titre et un tableau des compétences
    public byte[] exportPdf(String category) throws IOException {
        List<Skill> skills = getSkills(category);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf  = new PdfDocument(writer);
        Document doc     = new Document(pdf);

        PdfFont fontBold    = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal  = PdfFontFactory.createFont("Helvetica");

        // ── Titre
        Paragraph titre = new Paragraph("SkillForge")
                .setFont(fontBold)
                .setFontSize(24)
                .setFontColor(ColorConstants.BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4);
        doc.add(titre);

        // ── Sous-titre (catégorie ou "Toutes les compétences")
        String sousTitreText = (category == null || category.isBlank())
                ? "Toutes les compétences"
                : "Catégorie : " + category;
        Paragraph sousTitre = new Paragraph(sousTitreText)
                .setFont(fontNormal)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        doc.add(sousTitre);

        // ── Tableau
        // 3 colonnes : Nom (50%), Catégorie (30%), Niveau (20%)
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 30, 20}))
                .useAllAvailableWidth();

        // En-tête du tableau
        for (String header : new String[]{"Compétence", "Catégorie", "Niveau"}) {
            table.addHeaderCell(
                new Cell().add(new Paragraph(header).setFont(fontBold).setFontSize(11))
                          .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(74, 114, 196))
                          .setFontColor(ColorConstants.WHITE)
                          .setPadding(8)
            );
        }

        // Lignes alternées pour la lisibilité
        com.itextpdf.kernel.colors.DeviceRgb rowAlt =
                new com.itextpdf.kernel.colors.DeviceRgb(236, 238, 242);

        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            boolean isAlt = i % 2 == 1;

            Cell cellNom = new Cell().add(new Paragraph(s.getName()).setFont(fontBold).setFontSize(10)).setPadding(7);
            Cell cellCat = new Cell().add(new Paragraph(s.getCategory() != null ? s.getCategory() : "-").setFont(fontNormal).setFontSize(10)).setPadding(7);
            Cell cellNiv = new Cell().add(new Paragraph(getLevelString(s.getLevel())).setFont(fontNormal).setFontSize(10)).setPadding(7).setTextAlignment(TextAlignment.CENTER);

            if (isAlt) {
                cellNom.setBackgroundColor(rowAlt);
                cellCat.setBackgroundColor(rowAlt);
                cellNiv.setBackgroundColor(rowAlt);
            }

            table.addCell(cellNom);
            table.addCell(cellCat);
            table.addCell(cellNiv);
        }

        doc.add(table);

        // ── Pied de page
        Paragraph footer = new Paragraph("Exporté depuis SkillForge — " + java.time.LocalDate.now())
                .setFont(fontNormal)
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(16);
        doc.add(footer);

        doc.close();
        return baos.toByteArray();
    }

    // Convertit un niveau numérique en étoiles textuelles ex: 3 → "★★★☆☆"
    //private String getStars(int level) {
    //    return "★".repeat(Math.max(0, level)) + "☆".repeat(Math.max(0, 5 - level));
    //}

    private String getLevelString(int level) {
        switch (level) {
            case 0: return "Non évalué";
            case 1: return "Débutant";
            case 2: return "Intermédiaire";
            case 3: return "Avancé";
            case 4: return "Expert";
            case 5: return "Maîtrisé";
            default: return "Niveau " + level;
        }
    }
}