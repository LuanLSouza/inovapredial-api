package com.inovapredial.specification;

import com.inovapredial.dto.MaintenancePlanFilterDTO;
import com.inovapredial.model.MaintenancePlan;
import com.inovapredial.model.OwnUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MaintenancePlanSpecification {

    public static Specification<MaintenancePlan> withFilters(MaintenancePlanFilterDTO filter, OwnUser user) {
        return (Root<MaintenancePlan> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por usuário - apenas planos de manutenção do usuário logado
            predicates.add(criteriaBuilder.equal(root.get("ownUser"), user));

            if (filter != null) {
                if (filter.frequencyDays() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("frequencyDays"), filter.frequencyDays()));
                }

                if (filter.requiresShutdown() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("requiresShutdown"), filter.requiresShutdown()));
                }

                if (filter.description() != null && !filter.description().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            "%" + filter.description().toLowerCase() + "%"
                    ));
                }

                if (filter.maintenanceType() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("maintenanceType"), filter.maintenanceType()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
