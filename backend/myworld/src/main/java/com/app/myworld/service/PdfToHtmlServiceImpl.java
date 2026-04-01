package com.app.myworld.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfToHtmlServiceImpl implements PdfToHtmlService {
    
    private static final String UPLOADS_SEGMENT = "/api/uploads/";
    private static final String SAFE_UPLOAD_FILENAME_REGEX = "[A-Za-z0-9._-]+\\.[A-Za-z0-9]{1,10}";

    private final UploadService uploadService;

    @Override
    public String toHtml(String chapterContent) {
        if (chapterContent == null || chapterContent.isBlank()) {
            return "";
        }

        String uploadFilename = extractUploadFilenameFromContent(chapterContent);
        if (uploadFilename != null && uploadFilename.toLowerCase().endsWith(".pdf")) {
            Resource resource = uploadService.loadAsResource(uploadFilename);
            if (resource == null) {
                throw new IllegalArgumentException("Uploaded PDF not found: " + uploadFilename);
            }
            try (InputStream in = resource.getInputStream()) {
                return pdfToHtml(in);
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to read uploaded PDF: " + uploadFilename, ex);
            }
        }

        return TextToHtmlFormatter.formatToHtmlParagraphs(chapterContent);
    }

    @Override
    public String extractUploadFilenameFromContent(String content) {
        int idx = content.indexOf(UPLOADS_SEGMENT);
        if (idx < 0) {
            return null;
        }
        String after = content.substring(idx + UPLOADS_SEGMENT.length());
        int slash = after.indexOf('/');
        String filename = slash >= 0 ? after.substring(0, slash) : after;
        filename = filename.strip();
        if (filename.isEmpty()) {
            return null;
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return null;
        }
        if (!filename.matches(SAFE_UPLOAD_FILENAME_REGEX)) {
            return null;
        }
        return filename;
    }

    private static String pdfToHtml(InputStream pdfInputStream) {
        byte[] pdfBytes;
        try {
            pdfBytes = pdfInputStream.readAllBytes();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read PDF bytes", ex);
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setAddMoreFormatting(true);
            stripper.setLineSeparator("\n");
            stripper.setWordSeparator(" ");

            String extractedText = stripper.getText(document);
            return TextToHtmlFormatter.formatToHtmlParagraphs(extractedText);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to parse PDF", ex);
        }
    }
}
