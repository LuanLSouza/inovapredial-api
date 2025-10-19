package com.inovapredial.controller;

import com.inovapredial.dto.WorkOrderFilterDTO;
import com.inovapredial.dto.requests.WorkOrderInventoryRequestDTO;
import com.inovapredial.dto.requests.WorkOrderRequestDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.responses.WorkOrderInventoryResponseDTO;
import com.inovapredial.dto.responses.WorkOrderResponseDTO;
import com.inovapredial.mapper.WorkOrderMapper;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.service.WorkOrderService;
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

import java.util.List;

@Hidden
@RestController
@RequestMapping("work-orders")
@RequiredArgsConstructor
@Tag(name = "Work Orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new work order")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Work order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building, Equipment or Employee not found")
    })
    public WorkOrderResponseDTO create(@Valid @RequestBody WorkOrderRequestDTO dto,
                                       @RequestParam String buildingId) {
        WorkOrder created = workOrderService.create(dto, buildingId);
        return workOrderMapper.toResponseDTO(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing work order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Work order updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order, Equipment or Employee not found")
    })
    public WorkOrderResponseDTO update(@PathVariable String id,
                                      @Valid @RequestBody WorkOrderRequestDTO dto,
                                      @RequestParam String buildingId) {
        WorkOrder updated = workOrderService.update(id, dto, buildingId);
        return workOrderMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get work order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Work order retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order or Building not found")
    })
    public WorkOrderResponseDTO findById(@PathVariable String id, 
                                        @RequestParam String buildingId) {
        WorkOrder workOrder = workOrderService.findByIdAndBuilding(id, buildingId);
        return workOrderMapper.toResponseDTO(workOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete work order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Work order deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order or Building not found")
    })
    public void delete(@PathVariable String id, 
                      @RequestParam String buildingId) {
        workOrderService.delete(id, buildingId);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search work orders with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Work orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<WorkOrderResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) WorkOrderFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "openingDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        if (filter == null) {
            filter = new WorkOrderFilterDTO(null, null, null, null, null, null, null, null, null, null, null, null);
        }
        
        return workOrderService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{workOrderId}/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add inventory item to work order")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Inventory item added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient stock"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order, Inventory or Building not found")
    })
    public WorkOrderInventoryResponseDTO addInventoryItem(@PathVariable String workOrderId,
                                                          @Valid @RequestBody WorkOrderInventoryRequestDTO dto,
                                                          @RequestParam String buildingId) {
        return workOrderService.addInventoryItem(workOrderId, dto, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{workOrderId}/inventory/{inventoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove inventory item from work order")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Inventory item removed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order, Inventory item or Building not found")
    })
    public void removeInventoryItem(@PathVariable String workOrderId,
                                    @PathVariable String inventoryId,
                                    @RequestParam String buildingId) {
        workOrderService.removeInventoryItem(workOrderId, inventoryId, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{workOrderId}/inventory")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all inventory items from work order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Work order or Building not found")
    })
    public List<WorkOrderInventoryResponseDTO> getWorkOrderInventory(@PathVariable String workOrderId,
                                                                     @RequestParam String buildingId) {
        return workOrderService.getWorkOrderInventory(workOrderId, buildingId);
    }
}
