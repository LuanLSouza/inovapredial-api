package com.inovapredial.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarResponseDTO(
        UUID id,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}

