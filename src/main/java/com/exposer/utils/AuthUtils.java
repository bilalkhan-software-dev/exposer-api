package com.exposer.utils;


import com.exposer.constants.AppConstants;
import com.exposer.dao.interfaces.UserDao;
import com.exposer.exception.InvalidTokenException;
import com.exposer.exception.JwtExpiredException;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.entity.User;
import com.exposer.models.entity.enums.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER_PREFIX;

@Component
@Slf4j
public class AuthUtils {

    private final UserDao userDao;
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public AuthUtils(UserDao userDao) {
        this.userDao = userDao;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getUserRoles(String token) {
        Claims claims = extractClaimsFromToken(token);
        log.info("Claims : {}", claims);
        log.info("Username : {}", claims.getSubject());
        log.info("Authorities : {}", claims.get("role"));
        return claims.get("role", String.class);
    }

    public String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole().toString());

        return Jwts.builder()
                .subject(user.getUsername())
                .claims().add(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(AppConstants.getJwtExpirationDate())
                .issuer(apiBaseUrl)
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    private Claims extractClaimsFromToken(String token) {
        try {
            if (token.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
                token = token.substring(AUTHORIZATION_HEADER_PREFIX.length());
            }
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.info("Token expired: {} msg: {}", token, e.getMessage());
            throw new JwtExpiredException("Token is expired");
        } catch (JwtException e) {
            log.info("Invalid token: {} msg: {}", token, e.getMessage());
            throw new InvalidTokenException("Your Access Token is not valid");
        } catch (Exception e) {
            log.error("Exception occurred while trying to extract the claims from the token \n Details: {}", e.getMessage());
            throw e;
        }
    }


    private boolean isTokenExpired(String token) {
        Claims claims = extractClaimsFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        boolean tokenExpired = isTokenExpired(token);
        return username.equals(userDetails.getUsername()) && !tokenExpired;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = this.extractClaimsFromToken(token);
        return claims.getSubject();
    }

    public AuthProviderType getProviderTypeFromRegistrationId(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProviderType.GOOGLE;
            case "facebook" -> AuthProviderType.FACEBOOK;
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    // extracting oauth2 provider id
    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId) {
        String providerId = switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            case "github", "facebook" -> Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
            default -> {
                log.error("Unsupported OAuth2 provider: {}", registrationId);
                throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
            }
        };

        if (providerId == null || providerId.isBlank()) {
            log.error("Unable to determine providerId for provider: {}", registrationId);
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }

        return providerId;
    }

    public User getUserFromToken(String jwt) {
        log.info("Getting user from token: {}", jwt);

        String username = getUsernameFromToken(jwt);
        log.info("Extracted username: {}", username);


        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info("User found: ID={}, Username={}, Name={}",
                    user.getId(), user.getEmail(), user.getFullName());
            return user;
        } else {
            log.error("No user found for username: {}", username);
            throw new ResourceNotFoundException("User not found for username: " + username);
        }
    }
}
