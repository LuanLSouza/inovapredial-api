package com.inovapredial.repository;

import com.inovapredial.model.OwnUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface OwnUserRepository extends JpaRepository<OwnUser, UUID> {
    UserDetails findByEmail(String email);
}
