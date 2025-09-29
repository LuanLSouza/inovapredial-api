package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.BuildingType;
import jakarta.validation.constraints.NotBlank;

public record BuildingRequestDTO(
        @NotBlank(message = "Campo obrigatório") String name,
         BuildingType buildingType,
        Integer constructionYear,
        String description,
        AddressRequestDTO addressRequest
) {
}
