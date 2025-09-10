package com.inovapredial.service;

import com.inovapredial.model.OwnUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextService {
    private final OwnUserService service;

    public OwnUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()){
            OwnUser userDetails = (OwnUser) authentication.getPrincipal();
            return (OwnUser) service.loadUserByEmail(userDetails.getEmail());
        }

        return null;
    }
}
