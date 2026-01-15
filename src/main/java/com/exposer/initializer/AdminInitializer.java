package com.exposer.initializer;

import com.exposer.dao.interfaces.UserDao;
import com.exposer.models.entity.User;
import com.exposer.models.entity.enums.AccountStatus;
import com.exposer.models.entity.enums.Auth_Role;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "admin.create",havingValue = "true")
public class AdminInitializer implements CommandLineRunner {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.info.username}")
    private String username;

    @Value("${admin.info.email}")
    private String email;

    @Value("${admin.info.password}")
    private String password;

    @Value("${admin.info.fullName}")
    private String fullName;

    @Override
    public void run(String @NonNull ... args) {

        validateAdminProperties();

        userDao.findByUsernameOrEmail(username).ifPresentOrElse(
                _ -> log.info("Admin already exists. Skipping creation."),
                () -> {
                    log.info("Creating admin...");
                    User user = User.builder()
                            .username(username)
                            .email(email)
                            .fullName(fullName)
                            .password(passwordEncoder.encode(password))
                            .role(Auth_Role.ROLE_ADMIN)
                            .accountStatus(AccountStatus.ACTIVE)
                            .emailVerifiedAt(Instant.now())
                            .build();
                    userDao.save(user);
                    log.info("Admin created successfully");
                });
    }

    private void validateAdminProperties() {
        log.debug("Validating admin properties...");

        // Check for missing or empty properties
        if (!(StringUtils.hasText(username) && StringUtils.hasText(email) && StringUtils.hasText(password))) {
            log.error("Admin properties are not properly configured");
            throw new ValidationException("Admin properties are not properly configured");
        }

        if (!StringUtils.hasText(fullName)) {
            log.debug("Admin :: username:{} email:{} fullName:{}", username, email, fullName);
            log.warn("Admin full name is not configured or empty, using username as fallback");
            fullName = username.toUpperCase();
        }


        log.debug("Admin properties validated successfully");
    }
}
