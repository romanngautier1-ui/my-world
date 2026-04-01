package com.app.myworld.dto.authdto;

public record ResetPasswordRequest
(
    String token, 
    String newPassword
) {}
