package com.inovapredial.security;

import com.inovapredial.repository.OwnUserRepository;
import com.inovapredial.service.OwnUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final OwnUserService ownUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token  = recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);
            if (!login.isEmpty()) {
                try {
                    UserDetails user = ownUserService.loadUserByEmail(login);
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
