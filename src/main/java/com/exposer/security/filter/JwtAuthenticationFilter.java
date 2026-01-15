package com.exposer.security.filter;

import com.exposer.handler.GenericResponseHandler;
import com.exposer.security.CustomUserDetailService;
import com.exposer.utils.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;
import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService customUserService;
    private final AuthUtils authUtils;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/**",
            "/api/v1/auth/**",
            "/oauth2/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/api/v1/public/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String requestURI = request.getRequestURI();
            log.debug("JWT Filter processing request: {} {}", request.getMethod(), requestURI);

            final String header = request.getHeader(AUTHORIZATION_HEADER);

            if (header != null && header.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
                log.debug("Authorization header found");
                String token = header.substring(AUTHORIZATION_HEADER_PREFIX.length()).trim();

                if (token.isEmpty()) {
                    log.warn("Empty JWT token provided");
                    filterChain.doFilter(request, response);
                    return;
                }

                log.debug("Extracting username from token");
                String username = authUtils.getUsernameFromToken(token);
                log.debug("Username extracted: {}", username);

                String role = authUtils.getUserRoles(token);
                log.debug("User role extracted: {}", role);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.debug("Loading user details for username: {}", username);
                    UserDetails userDetails = customUserService.loadUserByUsername(username);

                    log.debug("Validating JWT token");
                    if (authUtils.validateToken(token, userDetails)) {
                        log.info("JWT token validation successful for user: {}", username);

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities
                        );

                        log.debug("Setting authentication in SecurityContext for user: {}", username);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("User {} authenticated successfully with role: {}", username, role);
                    } else {
                        log.warn("JWT token validation failed for user: {}", username);
                    }
                } else if (username != null) {
                    log.debug("Authentication already exists in SecurityContext for user: {}", username);
                }
            } else {
                log.debug("No authorization header found or invalid format");
            }
        } catch (Exception e) {
            log.error("JWT Authentication filter error: {}", e.getMessage());
            GenericResponseHandler.jwtResponse(response, e);
            return;
        }

        log.debug("Proceeding with filter chain");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // Check if request URI matches any public path pattern
        boolean shouldNotFilter = PUBLIC_PATHS.stream().anyMatch(pattern ->
                requestURI.startsWith(pattern.replace("/**", "")) ||
                        requestURI.equals(pattern.replace("/**", "")) ||
                        requestURI.equals(pattern)
        );

        if (shouldNotFilter) {
            log.debug("Skipping JWT filter for public path: {}", requestURI);
        }

        return shouldNotFilter;
    }
}