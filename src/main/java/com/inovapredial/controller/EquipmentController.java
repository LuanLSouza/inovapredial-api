package com.inovapredial.controller;

import com.inovapredial.dto.EquipmentFilterDTO;
import com.inovapredial.dto.EquipmentRequestDTO;
import com.inovapredial.dto.EquipmentResponseDTO;
import com.inovapredial.dto.PageResponseDTO;
import com.inovapredial.mapper.EquipmentMapper;
import com.inovapredial.model.Equipment;
import com.inovapredial.service.EquipmentService;
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
@RequestMapping("equipments")
@RequiredArgsConstructor
@Tag(name = "Equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new equipment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Equipment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public EquipmentResponseDTO create(@Valid @RequestBody EquipmentRequestDTO dto, 
                                      @RequestParam String buildingId) {
        Equipment created = equipmentService.create(dto, buildingId);
        return equipmentMapper.toResponseDTO(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing equipment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment or Building not found")
    })
    public EquipmentResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody EquipmentRequestDTO dto,
                                      @RequestParam String buildingId) {
        Equipment updated = equipmentService.update(id, dto, buildingId);
        return equipmentMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get equipment by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipment retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment or Building not found")
    })
    public EquipmentResponseDTO findById(@PathVariable String id, 
                                        @RequestParam String buildingId) {
        Equipment equipment = equipmentService.findByIdAndBuilding(id, buildingId);
        return equipmentMapper.toResponseDTO(equipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete equipment by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Equipment deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment or Building not found")
    })
    public void delete(@PathVariable String id, 
                      @RequestParam String buildingId) {
        equipmentService.delete(id, buildingId);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search equipments with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipments retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<EquipmentResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) EquipmentFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "identification") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        if (filter == null) {
            filter = new EquipmentFilterDTO(null, null, null, null, null, null, null, null, null, null);
        }
        
        return equipmentService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }
}
