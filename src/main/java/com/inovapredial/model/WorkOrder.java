package com.inovapredial.model;

import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.model.enums.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "opening_date")
    private LocalDateTime openingDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @Column(name = "activity_status")
    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "maintenance_type")
    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

}
