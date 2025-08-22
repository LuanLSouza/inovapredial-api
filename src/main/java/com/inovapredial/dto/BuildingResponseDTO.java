package com.inovapredial.dto;

import com.inovapredial.model.enums.BuildingType;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record BuildingResponseDTO(
        UUID id,
        @NotBlank(message = "Campo obrigatório") String name,
        BuildingType buildingType,
        Integer constructionYear,
        String description,
        AddressResponseDTO address
) {
}
