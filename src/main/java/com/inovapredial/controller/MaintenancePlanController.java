package com.inovapredial.controller;

import com.inovapredial.dto.MaintenancePlanFilterDTO;
import com.inovapredial.dto.requests.MaintenancePlanRequestDTO;
import com.inovapredial.dto.responses.MaintenancePlanResponseDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.mapper.MaintenancePlanMapper;
import com.inovapredial.model.MaintenancePlan;
import com.inovapredial.service.MaintenancePlanService;
import io.swagger.v3.oas.annotations.Hidden;
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

@Hidden
@RestController
@RequestMapping("maintenance-plans")
@RequiredArgsConstructor
@Tag(name = "Maintenance Plans")
public class MaintenancePlanController {

    private final MaintenancePlanService maintenancePlanService;
    private final MaintenancePlanMapper maintenancePlanMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new maintenance plan")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Maintenance plan created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MaintenancePlanResponseDTO create(@Valid @RequestBody MaintenancePlanRequestDTO dto,
                                           @RequestParam String buildingId) {
        MaintenancePlan created = maintenancePlanService.create(dto, buildingId);
        return maintenancePlanMapper.toResponseDTO(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing maintenance plan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance plan updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Maintenance plan or Building not found")
    })
    public MaintenancePlanResponseDTO update(@PathVariable String id,
                                           @Valid @RequestBody MaintenancePlanRequestDTO dto,
                                           @RequestParam String buildingId) {
        MaintenancePlan updated = maintenancePlanService.update(id, dto, buildingId);
        return maintenancePlanMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get maintenance plan by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance plan retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Maintenance plan or Building not found")
    })
    public MaintenancePlanResponseDTO findById(@PathVariable String id, 
                                             @RequestParam String buildingId) {
        MaintenancePlan maintenancePlan = maintenancePlanService.findByIdAndBuilding(id, buildingId);
        return maintenancePlanMapper.toResponseDTO(maintenancePlan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete maintenance plan by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Maintenance plan deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Maintenance plan or Building not found")
    })
    public void delete(@PathVariable String id, 
                      @RequestParam String buildingId) {
        maintenancePlanService.delete(id, buildingId);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search maintenance plans with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance plans retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<MaintenancePlanResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) MaintenancePlanFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "description") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        if (filter == null) {
            filter = new MaintenancePlanFilterDTO(null, null, null, null);
        }
        
        return maintenancePlanService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }
}
