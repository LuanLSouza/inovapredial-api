package com.inovapredial.dto.requests;

import jakarta.validation.constraints.Email;

public record AuthenticationDTO(
        @Email
        String login,
        String password
) {
}
