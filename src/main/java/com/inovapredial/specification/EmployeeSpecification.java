package com.inovapredial.specification;

import com.inovapredial.dto.EmployeeFilterDTO;
import com.inovapredial.model.Employee;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> withFilters(EmployeeFilterDTO filter) {
        return (Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (filter.name() != null && !filter.name().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + filter.name().toLowerCase() + "%"
                    ));
                }

                if (filter.specialty() != null && !filter.specialty().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("specialty")),
                            "%" + filter.specialty().toLowerCase() + "%"
                    ));
                }

                if (filter.contact() != null && !filter.contact().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("contact")),
                            "%" + filter.contact().toLowerCase() + "%"
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

