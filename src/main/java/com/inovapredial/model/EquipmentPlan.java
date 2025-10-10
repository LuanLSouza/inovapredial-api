package com.inovapredial.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_plan")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EquipmentPlan {

    @EmbeddedId
    private EquipmentPlanId id;

    @ManyToOne
    @MapsId("planId")
    @JoinColumn(name = "plan_id", referencedColumnName = "id")
    private MaintenancePlan maintenancePlan;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private Equipment equipment;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "is_realized")
    private boolean realized;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;
}
