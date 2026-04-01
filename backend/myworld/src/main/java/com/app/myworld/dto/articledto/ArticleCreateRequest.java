package com.app.myworld.dto.articledto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ArticleCreateRequest(

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    String title,

    @NotBlank(message = "Le contenu est obligatoire")
    String content,

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    @Positive(message = "L'identifiant de l'utilisateur doit être positif")
    Long userId,

    // @NotBlank(message = "L'url de l'image est obligatoire")
    // @Size(min = 5, message = "L'url' doit contenir minimum 5 caractères")
    String urlImage,

    // @NotNull(message = "Le fichier image est obligatoire")
    MultipartFile imageFile
) {}
