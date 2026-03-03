package com.labsafety.system.auth;

/**
 * Test-only DTO to match the JSON shape expected by /api/auth/register.
 * Keep fields aligned with your existing AuthRequest in controller.
 */
public record RegisterRequest(
        String username,
        String email,
        String password,
        String role
) {
}
