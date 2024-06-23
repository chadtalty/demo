package com.chadtalty.demo.filter;

import com.chadtalty.demo.exception.MissingJwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtDecoder jwtDecoder;

    // List of endpoints to bypass JWT validation
    private static final List<String> SWAGGER_WHITELIST = Arrays.asList(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-resources",
            "/webjars",
            "/configuration",
            "/favicon.ico",
            "/api-docs/swagger-config",
            "/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        // Bypass JWT validation for Swagger endpoints
        if (SWAGGER_WHITELIST.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new MissingJwtTokenException("JWT token is missing or invalid");
            }

            String jwtToken = authorizationHeader.substring(7);
            log.debug("JWT Token: {}", jwtToken);

            Jwt jwt = jwtDecoder.decode(jwtToken);
            log.debug("Decoded JWT: {}", jwt.getClaims());

            // Set the jwt token as a request attribute
            request.setAttribute("jwt", jwtToken);

            List<String> roles = jwt.getClaimAsStringList("roles");
            List<GrantedAuthority> authorities =
                    roles.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());

            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (MissingJwtTokenException | JwtException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }
}
