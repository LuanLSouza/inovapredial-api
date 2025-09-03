package com.inovapredial.service;

import com.inovapredial.dto.OwnUserRequestDTO;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.OwnUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnUserService {
    private final OwnUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public void create(OwnUserRequestDTO dto){
        if (repository.findByEmail(dto.email()) != null) {
            throw new IllegalArgumentException("Este email já está em uso.");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        OwnUser user = OwnUser.builder()
                .username(dto.username())
                .email(dto.email())
                .password(encodedPassword)
                .role(dto.role())
                .build();

        repository.save(user);
    }

}
