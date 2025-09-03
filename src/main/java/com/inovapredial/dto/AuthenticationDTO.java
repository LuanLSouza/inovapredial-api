package com.inovapredial.dto;

import jakarta.validation.constraints.Email;

public record AuthenticationDTO(
        @Email
        String login,
        String password
) {
}
