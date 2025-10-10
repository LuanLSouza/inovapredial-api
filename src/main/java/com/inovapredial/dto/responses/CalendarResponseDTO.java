package com.inovapredial.dto.responses;

import java.time.LocalTime;
import java.util.UUID;

public record CalendarResponseDTO(
        UUID id,
        String description,
        Boolean monday,
        Boolean tuesday,
        Boolean wednesday,
        Boolean thursday,
        Boolean friday,
        Boolean saturday,
        Boolean sunday,
        LocalTime startTime,
        LocalTime endTime,
        Boolean hasBreak
) {
}

