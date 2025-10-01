package com.inovapredial.controller;

import com.inovapredial.dto.InventoryFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.InventoryRequestDTO;
import com.inovapredial.dto.responses.InventoryResponseDTO;
import com.inovapredial.mapper.InventoryMapper;
import com.inovapredial.model.Inventory;
import com.inovapredial.service.InventoryService;
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


@RestController
@RequestMapping("inventories")
@RequiredArgsConstructor
@Tag(name = "Inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new inventory item")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Inventory item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public InventoryResponseDTO create(@Valid @RequestBody InventoryRequestDTO dto,
                                      @RequestParam String buildingId) {
        Inventory created = inventoryService.create(dto, buildingId);
        return inventoryMapper.toResponseDTO(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing inventory item")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory item updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Inventory item or Building not found")
    })
    public InventoryResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody InventoryRequestDTO dto,
                                      @RequestParam String buildingId) {
        Inventory updated = inventoryService.update(id, dto, buildingId);
        return inventoryMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get inventory item by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory item retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Inventory item or Building not found")
    })
    public InventoryResponseDTO findById(@PathVariable String id, 
                                        @RequestParam String buildingId) {
        Inventory inventory = inventoryService.findByIdAndBuilding(id, buildingId);
        return inventoryMapper.toResponseDTO(inventory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete inventory item by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Inventory item deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Inventory item or Building not found")
    })
    public void delete(@PathVariable String id, 
                      @RequestParam String buildingId) {
        inventoryService.delete(id, buildingId);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search inventory items with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<InventoryResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) InventoryFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        if (filter == null) {
            filter = new InventoryFilterDTO(null, null, null);
        }
        
        return inventoryService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }
}

