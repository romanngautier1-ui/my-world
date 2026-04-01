package com.app.myworld.dto.bookdto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Requête de mise à jour partielle (PATCH).
 * Les champs sont optionnels ; lorsqu'ils sont absents (null), ils ne sont pas modifiés.
 */
public record BookUpdateRequest(
    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    @Pattern(regexp = ".*\\S.*", message = "Le titre ne doit pas être vide")
    String title,

    @Positive(message = "Le numéro doit être positif")
    Integer number,

    String description,
    String urlImage,
    MultipartFile imageFile
) {}
