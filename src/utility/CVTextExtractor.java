package utility;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CVTextExtractor {

    public static String extract(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return "";
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) return "";

        String lower = filePath.toLowerCase();
        try {
            if (lower.endsWith(".pdf")) {
                return extractFromPDF(file);
            }  else if (lower.endsWith(".txt")) {
                return new String(Files.readAllBytes(Paths.get(filePath)));
            }
        } catch (Exception e) {
            System.err.println("CVTextExtractor error for " + filePath + ": " + e.getMessage());
        }
        return "";
    }

    private static String extractFromPDF(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            return cleanText(text);
        }
    }


    private static String cleanText(String text) {
        if (text == null) return "";
        text = text.replaceAll("[\\r\\n\\t]+", " ");
        text = text.replaceAll("\\s{2,}", " ");
        text = text.replaceAll("[^\\x20-\\x7E]", " ");
        text = text.trim();
        return text;
    }
}