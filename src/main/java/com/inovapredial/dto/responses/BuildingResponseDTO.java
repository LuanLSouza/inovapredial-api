package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.BuildingType;

import java.util.UUID;

public record BuildingResponseDTO(
        UUID id,
        String name,
        BuildingType buildingType,
        Integer constructionYear,
        String description,
        AddressResponseDTO address
) {
}
