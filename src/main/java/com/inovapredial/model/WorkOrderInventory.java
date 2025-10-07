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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_order_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkOrderInventory {

    @EmbeddedId
    private WorkOrderInventoryId id;

    @ManyToOne
    @MapsId("workOrderId")
    @JoinColumn(name = "work_order_id", referencedColumnName = "id")
    private WorkOrder workOrder;

    @ManyToOne
    @MapsId("inventoryId")
    @JoinColumn(name = "inventory_id", referencedColumnName = "id")
    private Inventory inventory;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "output_date")
    private LocalDateTime outputDate;
}
