package com.app.myworld.dto.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email,

    @NotBlank(message = "Le mot de passe d'utilisateur est obligatoire")
    @Size(min = 8, max = 50, message = "Le mot de passe doit contenir entre 8 et 50 caractères")
    String password,

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    String username
) {}
