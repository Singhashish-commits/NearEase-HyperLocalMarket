package com.hymer.hymarket.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalRequestLogger extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalRequestLogger.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        logger.info("Incoming Request: [{} {}] from IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Sent Response: [{} {}] status: {} ({}ms)",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

    }
}
