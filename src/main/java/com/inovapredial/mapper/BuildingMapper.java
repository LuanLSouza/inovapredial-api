package com.inovapredial.mapper;

import com.inovapredial.dto.requests.AddressRequestDTO;
import com.inovapredial.dto.responses.AddressResponseDTO;
import com.inovapredial.dto.requests.BuildingRequestDTO;
import com.inovapredial.dto.responses.BuildingResponseDTO;
import com.inovapredial.model.Address;
import com.inovapredial.model.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    Building toEntity(BuildingRequestDTO dto);

    BuildingResponseDTO toResponseDTO(Building building);

    Address toEntity(AddressRequestDTO dto);

    AddressResponseDTO toResponse(Address address);

    @Mapping(target = "address",  ignore = true)
    void updateBuildingFromRequestDTO(BuildingRequestDTO dto, @MappingTarget Building building);

    void updateAddressFromRequestDTO(AddressRequestDTO dto, @MappingTarget Address address);


}
