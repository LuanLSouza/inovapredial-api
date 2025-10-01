package com.inovapredial.dto.responses;

import java.util.UUID;

public record EmployeeResponseDTO(
        UUID id,
        String name,
        String specialty,
        String contact,
        CalendarResponseDTO calendar,
        UUID buildingId
) {
}

