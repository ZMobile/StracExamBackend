package org.strac.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.strac.service.google.token.GoogleAccessTokenValidatorService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleAccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final GoogleAccessTokenValidatorService googleAccessTokenValidatorService;

    public GoogleAccessTokenAuthenticationFilter(GoogleAccessTokenValidatorService googleAccessTokenValidatorService) {
        this.googleAccessTokenValidatorService = googleAccessTokenValidatorService;
    }

    // Define excluded paths that should skip validation
    private final List<String> excludedPaths = List.of(
            "/test",
            "/oauth2",
            "/oauth2/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip the filter for excluded paths
        if (excludedPaths.stream().anyMatch(requestPath::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7); // Remove "Bearer " prefix
            System.out.println("Access token: " + accessToken);
            try {
                System.out.println("Filtering...");
                // Validate the Google API access token using the service
                boolean isValid = googleAccessTokenValidatorService.validateGoogleAccessToken(accessToken);
                System.out.println("Is valid: " + isValid);
                if (isValid) {
                    // If token is valid, proceed with authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            accessToken,
                            null,
                            Collections.emptyList() // Add authorities if needed
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // If invalid, log the issue and proceed without authentication
                    System.err.println("Invalid Google access token.");
                }
            } catch (Exception e) {
                // Log token validation errors and proceed without authentication
                e.printStackTrace();
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
