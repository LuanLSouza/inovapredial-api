package com.inovapredial.model;

import com.inovapredial.model.enums.MaintenanceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "maintenance_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenancePlan {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(name = "frequency_days")
    private Integer frequencyDays;

    @Column(name = "requires_shutdown")
    private Boolean requiresShutdown;

    @Column(name = "description")
    private String description;

    @Column(name = "maintenance_type")
    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;
}
