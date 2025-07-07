package com.flipkart.order.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JwtAuthFilter executing for URI: " + request.getRequestURI());
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extract token after "Bearer "

            try {
                // Call Auth Service to validate token
                ResponseEntity<Map> result = restTemplate.getForEntity(
                         "http://localhost:9898/auth/validate?token=" + token,
                        Map.class
                );

                Map<String, Object> responseBody = result.getBody();

                System.out.println(responseBody);

                if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
                    String username = (String) responseBody.get("username");
                    String role = (String) responseBody.get("role");

                    // Create authorities (assuming single role)
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    // Set authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Log the exception for debugging
                logger.error("Failed to validate token: {}", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
