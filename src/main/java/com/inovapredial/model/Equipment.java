package com.inovapredial.model;

import com.inovapredial.Calendar;
import com.inovapredial.model.enums.Criticality;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.model.enums.EquipmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(name = "identification")
    private String identification;

    @Column(name = "description")
    private String description;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "classification")
    @Enumerated(EnumType.STRING)
    private EquipmentType classification;

    @Column(name = "location")
    private String location;

    @Column(name = "criticality")
    @Enumerated(EnumType.STRING)
    private Criticality criticality;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_end_date")
    private LocalDate warrantyEndDate;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "equipment_status")
    @Enumerated(EnumType.STRING)
    private EquipmentStatus equipmentStatus;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "group")
    private String group;

    @Column(name = "model")
    private String model;

    @Column(name = "cost_center")
    private String costCenter;

    @ManyToOne
    @JoinColumn(name = "operation_calendar_id")
    private Calendar calendar;

    @ManyToOne
    @JoinColumn(name = "ownuser_id")
    private OwnUser ownUser;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

}
