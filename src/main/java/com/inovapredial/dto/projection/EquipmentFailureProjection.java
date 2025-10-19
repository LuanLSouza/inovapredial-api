package com.inovapredial.dto.projection;

import java.time.LocalDateTime;

public interface EquipmentFailureProjection {
    String getEquipmentId();
    String getEquipmentName();
    String getEquipmentIdentification();
    String getCriticality();
    Long getFailureCount();
    Double getAverageDaysBetweenFailures();
    LocalDateTime getLastFailureDate();
}
