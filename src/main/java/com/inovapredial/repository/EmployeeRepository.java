package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {
    
    Optional<Employee> findByIdAndBuilding(UUID id, Building building);
}
