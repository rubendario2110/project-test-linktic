package com.testbackend.inventory.config;

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
        String key = req.getHeader("X-INTERNAL-API-KEY");
        if (!Objects.equals(key, props.getApiKey())) {
            res.sendError(401, "INVALID_API_KEY");
            return;
        }
        fc.doFilter(req, res);
    }
}
