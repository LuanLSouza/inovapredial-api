package com.inovapredial.dto.projection;

import com.inovapredial.model.enums.MaintenanceType;
import java.math.BigDecimal;

public interface MaintenanceTypeMetricProjection {
    MaintenanceType getMaintenanceType();
    Long getCount();
    BigDecimal getTotalCost();
    BigDecimal getAverageCost();
}
