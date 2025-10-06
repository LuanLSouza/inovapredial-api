package com.inovapredial.specification;

import com.inovapredial.dto.WorkOrderFilterDTO;
import com.inovapredial.model.Building;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.WorkOrder;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WorkOrderSpecification {

    public static Specification<WorkOrder> withFilters(WorkOrderFilterDTO filter, OwnUser currentUser) {
        return Specification.where(byUser(currentUser))
                .and(filter != null ? byDescription(filter.description()) : null)
                .and(filter != null ? byOpeningDateStart(filter.openingDateStart()) : null)
                .and(filter != null ? byOpeningDateEnd(filter.openingDateEnd()) : null)
                .and(filter != null ? byClosingDateStart(filter.closingDateStart()) : null)
                .and(filter != null ? byClosingDateEnd(filter.closingDateEnd()) : null)
                .and(filter != null ? byActivityStatus(filter.activityStatus()) : null)
                .and(filter != null ? byPriority(filter.priority()) : null)
                .and(filter != null ? byMaintenanceType(filter.maintenanceType()) : null)
                .and(filter != null ? byEquipmentId(filter.equipmentId()) : null)
                .and(filter != null ? byEmployeeId(filter.employeeId()) : null)
                .and(filter != null ? byTotalCostMin(filter.totalCostMin()) : null)
                .and(filter != null ? byTotalCostMax(filter.totalCostMax()) : null);
    }

    private static Specification<WorkOrder> byUser(OwnUser user) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<WorkOrder, Building> buildingJoin = root.join("building");
            Join<Building, OwnUser> usersJoin = buildingJoin.join("users");
            return criteriaBuilder.equal(usersJoin, user);
        };
    }

    private static Specification<WorkOrder> byDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%");
    }

    private static Specification<WorkOrder> byOpeningDateStart(LocalDateTime startDate) {
        if (startDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("openingDate"), startDate);
    }

    private static Specification<WorkOrder> byOpeningDateEnd(LocalDateTime endDate) {
        if (endDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("openingDate"), endDate);
    }

    private static Specification<WorkOrder> byClosingDateStart(LocalDateTime startDate) {
        if (startDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("closingDate"), startDate);
    }

    private static Specification<WorkOrder> byClosingDateEnd(LocalDateTime endDate) {
        if (endDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("closingDate"), endDate);
    }

    private static Specification<WorkOrder> byActivityStatus(Object activityStatus) {
        if (activityStatus == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("activityStatus"), activityStatus);
    }

    private static Specification<WorkOrder> byPriority(Object priority) {
        if (priority == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), priority);
    }

    private static Specification<WorkOrder> byMaintenanceType(Object maintenanceType) {
        if (maintenanceType == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("maintenanceType"), maintenanceType);
    }

    private static Specification<WorkOrder> byEquipmentId(Object equipmentId) {
        if (equipmentId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("equipment").get("id"), equipmentId);
    }

    private static Specification<WorkOrder> byEmployeeId(Object employeeId) {
        if (employeeId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employee").get("id"), employeeId);
    }

    private static Specification<WorkOrder> byTotalCostMin(BigDecimal minCost) {
        if (minCost == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("totalCost"), minCost);
    }

    private static Specification<WorkOrder> byTotalCostMax(BigDecimal maxCost) {
        if (maxCost == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("totalCost"), maxCost);
    }
}
