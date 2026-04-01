package com.app.myworld.service;

public interface PdfToHtmlService {

    /**
     * Returns safe, structured HTML for a chapter content.
     * If {@code chapterContent} is a URL to an uploaded PDF (/api/uploads/{file}.pdf),
     * this will extract text using PDFBox and format it into <p> blocks.
     * Otherwise, it formats the raw content as text paragraphs.
     */
    public String toHtml(String chapterContent);

    public String extractUploadFilenameFromContent(String content);
}
