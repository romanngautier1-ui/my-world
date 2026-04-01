package com.app.myworld.dto.chapterdto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Requête de mise à jour partielle (PATCH).
 * Les champs sont optionnels ; lorsqu'ils sont absents (null), ils ne sont pas modifiés.
 */
public record ChapterUpdateRequest(

    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    @Pattern(regexp = ".*\\S.*", message = "Le titre ne doit pas être vide")
    String title,

    @Positive(message = "Le numéro du chapitre doit être positif")
    Integer number,

    @Size(min = 1, message = "Le contenu ne doit pas être vide")
    @Pattern(regexp = "(?s).*\\S.*", message = "Le contenu ne doit pas être vide")
    String content,

    @Positive(message = "L'identifiant du book doit être positif")
    Long bookId,

    MultipartFile pdfFile
) {}
