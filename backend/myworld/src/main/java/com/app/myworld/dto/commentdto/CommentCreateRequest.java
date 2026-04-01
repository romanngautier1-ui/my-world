package com.app.myworld.dto.commentdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 1, max = 2000, message = "Le commentaire doit contenir entre 1 et 2000 caractères")
    String content,

    @NotNull(message = "La note du commentaire est obligatoire")
    @Min(value = 1, message = "La note doit être comprise entre 1 et 10 inclus")
    @Max(value = 10, message = "La note doit être comprise entre 1 et 10 inclus")
    Integer rating,

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    @Positive(message = "L'identifiant de l'utilisateur doit être positif")
    Long userId,

    @NotNull(message = "L'identifiant de chapitre est obligatoire")
    @Positive(message = "L'identifiant de chapitre doit être positif")
    Long chapterId
) {}
