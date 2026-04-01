package com.app.myworld.dto.bookdto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 150, message = "Le titre doit contenir entre 1 et 150 caractères")
    String title,

    @NotNull(message = "Le numéro est obligatoire")
    @Positive(message = "Le numéro doit être positif")
    Integer number,

    String description,
    String urlImage,
    MultipartFile imageFile
) {}
