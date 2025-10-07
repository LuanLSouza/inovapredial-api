package com.inovapredial.specification;

import com.inovapredial.dto.TaskFilterDTO;
import com.inovapredial.model.Building;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.Task;
import com.inovapredial.model.WorkOrder;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> withFilters(TaskFilterDTO filter, OwnUser currentUser) {
        return Specification.where(byUser(currentUser))
                .and(filter != null ? byTitle(filter.title()) : null)
                .and(filter != null ? byDescription(filter.description()) : null)
                .and(filter != null ? byActivityStatus(filter.activityStatus()) : null)
                .and(filter != null ? byEstimatedTimeMin(filter.estimatedTimeMin()) : null)
                .and(filter != null ? byEstimatedTimeMax(filter.estimatedTimeMax()) : null)
                .and(filter != null ? byStartDateStart(filter.startDateStart()) : null)
                .and(filter != null ? byStartDateEnd(filter.startDateEnd()) : null)
                .and(filter != null ? byEndDateStart(filter.endDateStart()) : null)
                .and(filter != null ? byEndDateEnd(filter.endDateEnd()) : null)
                .and(filter != null ? byTimeSpentMin(filter.timeSpentMin()) : null)
                .and(filter != null ? byTimeSpentMax(filter.timeSpentMax()) : null)
                .and(filter != null ? byCostMin(filter.costMin()) : null)
                .and(filter != null ? byCostMax(filter.costMax()) : null)
                .and(filter != null ? byWorkOrderId(filter.workOrderId()) : null)
                .and(filter != null ? byEmployeeId(filter.employeeId()) : null);
    }

    private static Specification<Task> byUser(OwnUser user) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<Task, Building> buildingJoin = root.join("building");
            Join<Building, OwnUser> usersJoin = buildingJoin.join("users");
            return criteriaBuilder.equal(usersJoin, user);
        };
    }

    private static Specification<Task> byTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%");
    }

    private static Specification<Task> byDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%");
    }

    private static Specification<Task> byActivityStatus(Object activityStatus) {
        if (activityStatus == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("activityStatus"), activityStatus);
    }

    private static Specification<Task> byEstimatedTimeMin(Integer min) {
        if (min == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("estimatedTime"), min);
    }

    private static Specification<Task> byEstimatedTimeMax(Integer max) {
        if (max == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("estimatedTime"), max);
    }

    private static Specification<Task> byStartDateStart(LocalDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), start);
    }

    private static Specification<Task> byStartDateEnd(LocalDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), end);
    }

    private static Specification<Task> byEndDateStart(LocalDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), start);
    }

    private static Specification<Task> byEndDateEnd(LocalDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), end);
    }

    private static Specification<Task> byTimeSpentMin(Integer min) {
        if (min == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("timeSpent"), min);
    }

    private static Specification<Task> byTimeSpentMax(Integer max) {
        if (max == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("timeSpent"), max);
    }

    private static Specification<Task> byCostMin(BigDecimal min) {
        if (min == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), min);
    }

    private static Specification<Task> byCostMax(BigDecimal max) {
        if (max == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("cost"), max);
    }

    private static Specification<Task> byWorkOrderId(Object workOrderId) {
        if (workOrderId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workOrder").get("id"), workOrderId);
    }

    private static Specification<Task> byEmployeeId(Object employeeId) {
        if (employeeId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employee").get("id"), employeeId);
    }
}


