package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OwnUserRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        String username,
        @NotBlank(message = "Campo obrigatório")
        String password,
        @NotBlank(message = "Campo obrigatório")
        @Email(message = "Email inválido")
        String email,
        UserRole role
) {
}
