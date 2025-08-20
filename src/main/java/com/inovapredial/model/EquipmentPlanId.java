package com.inovapredial.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class EquipmentPlanId implements Serializable {
    private UUID equipmentId;
    private UUID planId;
}
