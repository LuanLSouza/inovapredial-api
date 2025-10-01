package com.inovapredial.specification;

import com.inovapredial.dto.InventoryFilterDTO;
import com.inovapredial.model.Inventory;
import com.inovapredial.model.OwnUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InventorySpecification {

    public static Specification<Inventory> withFilters(InventoryFilterDTO filter, OwnUser user) {
        return (Root<Inventory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por usuário - apenas inventários do building do usuário logado
            // (removido filtro por employee específico, pois agora é opcional)

            if (filter != null) {
                if (filter.itemType() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("itemType"), filter.itemType()));
                }

                if (filter.name() != null && !filter.name().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + filter.name().toLowerCase() + "%"
                    ));
                }

                if (filter.employeeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("employee").get("id"), filter.employeeId()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

