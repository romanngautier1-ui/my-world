package com.app.myworld.dto.commentdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(

    @Size(min = 1, max = 2000, message = "Le commentaire doit contenir entre 1 et 2000 caractères")
    @Pattern(regexp = "(?s).*\\S.*", message = "Le commentaire ne doit pas être vide")
    String content,

    @Min(value = 1, message = "La note doit être comprise entre 1 et 10 inclus")
    @Max(value = 10, message = "La note doit être comprise entre 1 et 10 inclus")
    Integer rating
) {}
