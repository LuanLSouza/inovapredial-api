package com.inovapredial.controller;

import com.inovapredial.dto.BuildingFilterDTO;
import com.inovapredial.dto.BuildingRequestDTO;
import com.inovapredial.dto.BuildingResponseDTO;
import com.inovapredial.dto.PageResponseDTO;
import com.inovapredial.mapper.BuildingMapper;
import com.inovapredial.model.Building;
import com.inovapredial.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingResponseDTO create(@Valid @RequestBody BuildingRequestDTO dto){
        return  buildingMapper.toResponseDTO(buildingService.create(dto));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BuildingResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody BuildingRequestDTO dto) {

        Building updated = buildingService.update(id, dto);
        return buildingMapper.toResponseDTO(updated);

    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BuildingResponseDTO findById(@PathVariable String id) {
        Building building = buildingService.findById(id);
        return buildingMapper.toResponseDTO(building);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        buildingService.delete(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BuildingResponseDTO> findAll() {
        return buildingService.findAll();
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDTO<BuildingResponseDTO> findAllWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String buildingType,
            @RequestParam(required = false) Integer constructionYear,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String street,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        BuildingFilterDTO filter = BuildingFilterDTO.builder()
                .name(name)
                .buildingType(buildingType != null ? com.inovapredial.model.enums.BuildingType.valueOf(buildingType) : null)
                .constructionYear(constructionYear)
                .description(description)
                .street(street)
                .number(number)
                .district(district)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .build();
        
        return buildingService.findAllWithFilters(filter, page, size, sortBy, sortDirection);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDTO<BuildingResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) BuildingFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        if (filter == null) {
            filter = new BuildingFilterDTO();
        }
        
        return buildingService.findAllWithFilters(filter, page, size, sortBy, sortDirection);
    }

}
