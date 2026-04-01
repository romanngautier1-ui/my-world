package com.app.myworld.dto.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Requête de mise à jour partielle (PATCH).
 * Les champs sont optionnels ; lorsqu'ils sont absents (null), ils ne sont pas modifiés.
 */
public record UserUpdateRequest(

    @Email(message = "Format d'email invalide")
    @Pattern(regexp = ".*\\S.*", message = "L'email ne doit pas être vide")
    String email,

    @Size(min = 8, max = 50, message = "Le mot de passe doit contenir entre 8 et 50 caractères")
    @Pattern(regexp = ".*\\S.*", message = "Le mot de passe ne doit pas être vide")
    String password,

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = ".*\\S.*", message = "Le nom d'utilisateur ne doit pas être vide")
    String username
) {}
