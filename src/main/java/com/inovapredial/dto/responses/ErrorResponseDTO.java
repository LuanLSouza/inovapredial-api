package com.inovapredial.dto.responses;

import com.inovapredial.dto.FieldErrorsDTO;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorsDTO> fieldErrors
        ) {
}
