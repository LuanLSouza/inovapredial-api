package com.inovapredial.specification;

import com.inovapredial.dto.BuildingFilterDTO;
import com.inovapredial.model.Building;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.enums.BuildingType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BuildingSpecification {

    public static Specification<Building> withFilters(BuildingFilterDTO filter, OwnUser currentUser) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Building, OwnUser> userJoin = root.join("users");
            predicates.add(criteriaBuilder.equal(userJoin.get("id"), currentUser.getId()));

            // Filtros do Building
            if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            if (filter.getBuildingType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("buildingType"), filter.getBuildingType()));
            }

            if (filter.getConstructionYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("constructionYear"), filter.getConstructionYear()));
            }

            if (filter.getDescription() != null && !filter.getDescription().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + filter.getDescription().toLowerCase() + "%"
                ));
            }

            // Filtros do Address (join com a tabela address)
            if (filter.getStreet() != null && !filter.getStreet().trim().isEmpty()) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(addressJoin.get("street")),
                    "%" + filter.getStreet().toLowerCase() + "%"
                ));
            }

            if (filter.getNumber() != null) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.equal(addressJoin.get("number"), filter.getNumber()));
            }

            if (filter.getDistrict() != null && !filter.getDistrict().trim().isEmpty()) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(addressJoin.get("district")),
                    "%" + filter.getDistrict().toLowerCase() + "%"
                ));
            }

            if (filter.getCity() != null && !filter.getCity().trim().isEmpty()) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(addressJoin.get("city")),
                    "%" + filter.getCity().toLowerCase() + "%"
                ));
            }

            if (filter.getState() != null && !filter.getState().trim().isEmpty()) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(addressJoin.get("state")),
                    "%" + filter.getState().toLowerCase() + "%"
                ));
            }

            if (filter.getZipCode() != null && !filter.getZipCode().trim().isEmpty()) {
                Join<Building, com.inovapredial.model.Address> addressJoin = root.join("address");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(addressJoin.get("zipCode")),
                    "%" + filter.getZipCode().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

