package com.app.myworld.dto.articledto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArticleUpdateRequest(

    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    @Pattern(regexp = ".*\\S.*", message = "Le titre ne doit pas être vide")
    String title,

    @Size(min = 1, message = "Le contenu ne doit pas être vide")
    @Pattern(regexp = "(?s).*\\S.*", message = "Le contenu ne doit pas être vide")
    String content,

    // @NotBlank(message = "L'url de l'image est obligatoire")
    // @Size(min = 5, message = "L'url' doit contenir minimum 5 caractères")
    String urlImage,

    // @NotNull(message = "Le fichier image est obligatoire")
    MultipartFile imageFile
) {}
