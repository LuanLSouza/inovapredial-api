package com.inovapredial.specification;

import com.inovapredial.dto.EquipmentFilterDTO;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.OwnUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSpecification {

    public static Specification<Equipment> withFilters(EquipmentFilterDTO filter, OwnUser user) {
        return (Root<Equipment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por usuário - apenas equipamentos do usuário logado
            predicates.add(criteriaBuilder.equal(root.get("ownUser"), user));

            if (filter != null) {
                if (filter.identification() != null && !filter.identification().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("identification")),
                            "%" + filter.identification().toLowerCase() + "%"
                    ));
                }

                if (filter.description() != null && !filter.description().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            "%" + filter.description().toLowerCase() + "%"
                    ));
                }

                if (filter.serialNumber() != null && !filter.serialNumber().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("serialNumber")),
                            "%" + filter.serialNumber().toLowerCase() + "%"
                    ));
                }

                if (filter.classification() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("classification"), filter.classification()));
                }

                if (filter.location() != null && !filter.location().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("location")),
                            "%" + filter.location().toLowerCase() + "%"
                    ));
                }

                if (filter.criticality() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("criticality"), filter.criticality()));
                }

                if (filter.equipmentStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("equipmentStatus"), filter.equipmentStatus()));
                }

                if (filter.group() != null && !filter.group().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("group")),
                            "%" + filter.group().toLowerCase() + "%"
                    ));
                }

                if (filter.model() != null && !filter.model().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("model")),
                            "%" + filter.model().toLowerCase() + "%"
                    ));
                }

                if (filter.costCenter() != null && !filter.costCenter().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("costCenter")),
                            "%" + filter.costCenter().toLowerCase() + "%"
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

