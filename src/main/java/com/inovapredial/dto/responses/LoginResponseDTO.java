package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.UserRole;

public record LoginResponseDTO(
        String token,
        String username,
        UserRole role
) {
}
