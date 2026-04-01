package com.app.myworld.dto.chapterdto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChapterCreateRequest(

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    String title,

    @NotNull(message = "Le numéro du chapitre est obligatoire")
    @Positive(message = "Le numéro du chapitre doit être positif")
    Integer number,

    // @NotBlank(message = "Le contenu est obligatoire")
    String content,

    @NotNull(message = "L'identifiant du book est obligatoire")
    @Positive(message = "L'identifiant du book doit être positif")
    Long bookId,

    MultipartFile pdfFile
) {}
