package com.inovapredial.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarRequestDTO(
        UUID id,
        
        @Length(max = 500, message = "O campo deve ter no máximo {max} caracteres")
        String description,
        
        LocalDateTime startTime,
        
        LocalDateTime endTime
) {
}

