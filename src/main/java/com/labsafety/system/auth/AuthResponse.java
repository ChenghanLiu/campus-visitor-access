package com.labsafety.system.auth;

public record AuthResponse(
        String token,
        String role
) {
}
