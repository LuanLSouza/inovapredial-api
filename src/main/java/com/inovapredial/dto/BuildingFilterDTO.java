package com.inovapredial.dto;

import com.inovapredial.model.enums.BuildingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingFilterDTO {
    private String name;
    private BuildingType buildingType;
    private Integer constructionYear;
    private String description;
    
    // Filtros do endereço
    private String street;
    private Integer number;
    private String district;
    private String city;
    private String state;
    private String zipCode;
}


