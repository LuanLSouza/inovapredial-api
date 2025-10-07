package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TaskStatusUpdateRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        @Length(max = 500, message = "O campo deve ter no máximo {max} caracteres")
        String reason
) {
}


