package com.app.myworld.dto.chapterreaddto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChapterReadCreateRequest(
    
    @NotNull(message = "L'identifiant du chapitre est obligatoire")
    @Positive(message = "L'identifiant du chapitre doit être positif")
    Long chapterId,

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    @Positive(message = "L'identifiant de l'utilisateur doit être positif")
    Long userId
) {}