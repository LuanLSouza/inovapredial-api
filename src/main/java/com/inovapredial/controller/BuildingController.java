package com.inovapredial.controller;

import com.inovapredial.dto.BuildingFilterDTO;
import com.inovapredial.dto.BuildingRequestDTO;
import com.inovapredial.dto.BuildingResponseDTO;
import com.inovapredial.dto.PageResponseDTO;
import com.inovapredial.mapper.BuildingMapper;
import com.inovapredial.model.Building;
import com.inovapredial.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("buildings")
@RequiredArgsConstructor
@Tag(name = "Buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new building")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Building created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public BuildingResponseDTO create(@Valid @RequestBody BuildingRequestDTO dto){
        return  buildingMapper.toResponseDTO(buildingService.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing building")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Building updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public BuildingResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody BuildingRequestDTO dto) {

        Building updated = buildingService.update(id, dto);
        return buildingMapper.toResponseDTO(updated);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get building by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Building retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public BuildingResponseDTO findById(@PathVariable String id) {
        Building building = buildingService.findById(id);
        return buildingMapper.toResponseDTO(building);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete building by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Building deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public void delete(@PathVariable String id) {
        buildingService.delete(id);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search buildings with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Buildings retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
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
