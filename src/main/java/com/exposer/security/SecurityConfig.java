package com.exposer.security;

import com.exposer.models.dto.response.ApiResponse;
import com.exposer.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;

@Configuration
@Slf4j
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->

                        authorizeRequests
                                .requestMatchers("/actuator/**",
                                        "/api/v1/auth/**",
                                        "/login/oauth2/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api/v1/public/**",
                                        "/swagger-ui.html"
                                )
                                .permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(oauth2Login ->
                        oauth2Login.successHandler(oAuthSuccessHandler)
                                .failureHandler((request, response, exception) -> {
                                    log.error("OAuth2 error: {}", exception.getMessage());
                                    handlerExceptionResolver.resolveException(request, response, null, exception);
                                }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint((request, response, authException) -> {

                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setCharacterEncoding(StandardCharsets.UTF_8);
                                    response.setContentType("application/json");
                                    Object body = ApiResponse.builder()
                                            .status("error")
                                            .httpStatus(HttpStatus.UNAUTHORIZED)
                                            .message("Need authentication. Please login to access the resources.")
                                            .data(authException.getMessage())
                                            .build();
                                    new ObjectMapper().writeValue(response.getOutputStream(), body);
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    log.error("Access denied: {}", accessDeniedException.getMessage());
                                    handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
                                }))
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl, "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of(AUTHORIZATION_HEADER, "Content-Type"));
        config.setExposedHeaders(List.of(AUTHORIZATION_HEADER));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
