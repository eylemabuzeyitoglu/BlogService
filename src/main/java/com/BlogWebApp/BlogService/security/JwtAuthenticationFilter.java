package com.BlogWebApp.BlogService.security;

import com.BlogWebApp.BlogService.client.AuthServiceClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Token var → AuthService'e doğrulat
            boolean isValid = Boolean.TRUE.equals(authServiceClient.validateToken(authHeader).getBody());

            if (!isValid) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            // Burada gerçek user bilgisi oluşturmak istersek Authentication set edebiliriz.
            // Basit haliyle sadece doğrulama yapıyoruz, user detayına gerek yok.

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}