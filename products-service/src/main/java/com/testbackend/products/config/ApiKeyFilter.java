package com.testbackend.products.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {
    private final ApiKeyProperties props;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc) throws ServletException, IOException {
        String path = req.getRequestURI();
        // Permitir todas las peticiones GET a endpoints de productos (incluyendo /products/{id})
        if (path.startsWith("/products") && "GET".equals(req.getMethod())) {
            fc.doFilter(req, res);
            return;
        }
        
        // Para todas las demás operaciones, requerir API key
        String key = req.getHeader("X-INTERNAL-API-KEY");
        if (key == null || !Objects.equals(key, props.getApiKey())) {
            res.sendError(401, "INVALID_API_KEY");
            return;
        }
        fc.doFilter(req, res);
    }
}
