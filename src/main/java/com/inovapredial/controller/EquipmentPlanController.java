package com.inovapredial.controller;

import com.inovapredial.dto.requests.EquipmentPlanRequestDTO;
import com.inovapredial.dto.requests.EquipmentPlanUpdateRequestDTO;
import com.inovapredial.dto.responses.EquipmentPlanResponseDTO;
import com.inovapredial.service.EquipmentPlanService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("equipment-plans")
@RequiredArgsConstructor
@Tag(name = "Equipment Plans")
public class EquipmentPlanController {

    private final EquipmentPlanService equipmentPlanService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add maintenance plan to equipment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Maintenance plan added to equipment successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or plan already associated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment, Maintenance plan or Building not found")
    })
    public EquipmentPlanResponseDTO addPlanToEquipment(@Valid @RequestBody EquipmentPlanRequestDTO dto,
                                                       @RequestParam String buildingId) {
        return equipmentPlanService.addPlanToEquipment(dto, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("equipment/{equipmentId}/maintenance-plan/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove maintenance plan from equipment")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Maintenance plan removed from equipment successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment plan relationship or Building not found")
    })
    public void removePlanFromEquipment(@PathVariable String equipmentId,
                                        @PathVariable String planId,
                                        @RequestParam String buildingId) {
        equipmentPlanService.removePlanFromEquipment(equipmentId, planId, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("equipment/{equipmentId}/maintenance-plan/{planId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update maintenance plan realization status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance plan status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment plan relationship or Building not found")
    })
    public EquipmentPlanResponseDTO updateRealized(@PathVariable String equipmentId,
                                                   @PathVariable String planId,
                                                   @Valid @RequestBody EquipmentPlanUpdateRequestDTO dto,
                                                   @RequestParam String buildingId) {
        return equipmentPlanService.updateRealized(equipmentId, planId, dto, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("equipment/{equipmentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all maintenance plans for an equipment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipment maintenance plans retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Equipment or Building not found")
    })
    public List<EquipmentPlanResponseDTO> getEquipmentPlans(@PathVariable String equipmentId,
                                                             @RequestParam String buildingId) {
        return equipmentPlanService.getEquipmentPlans(equipmentId, buildingId);
    }
}

