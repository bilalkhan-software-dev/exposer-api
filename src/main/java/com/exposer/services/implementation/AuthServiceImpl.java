package com.exposer.services.implementation;

import com.exposer.dao.interfaces.UserDao;
import com.exposer.exception.ExistDataException;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.SendNotificationEvent;
import com.exposer.models.dto.request.LoginRequest;
import com.exposer.models.dto.request.RegisterRequest;
import com.exposer.models.dto.response.AuthResponse;
import com.exposer.models.entity.User;
import com.exposer.models.entity.enums.AccountStatus;
import com.exposer.models.entity.enums.AuthProviderType;
import com.exposer.services.EmailService;
import com.exposer.services.interfaces.AuthService;
import com.exposer.templates.EmailSendingTemplates;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final UserDao userDao;
    private final AuthUtils authUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.base.url}")
    private String baseUrl;

    @Override
    @Transactional
    public void registerUser(RegisterRequest request) {
        log.info("Starting user registration for email: {}", request.getEmail());

        validateEmailAndUsername(request.getEmail(), request.getUsername());
        log.debug("Email and username validation passed");

        String verificationToken = CommonUtil.generateVerificationToken();
        log.debug("Generated verification token for user: {}", request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountStatus(AccountStatus.UNVERIFIED)
                .fullName(request.getFullName())
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenCreatedAt(LocalDateTime.now())
                .build();

        log.info("Creating new user entity for email: {}", request.getEmail());
        User saved = userDao.save(user);

        if (saved != null) {
            log.info("User saved successfully with ID: {}", saved.getId());
            String verificationLink = CommonUtil.getVerificationLink(baseUrl, user.getEmail(), verificationToken);
            String body = EmailSendingTemplates.sendVerificationEmail(user.getFullName(), verificationLink);
            sendEmailNotification(user.getEmail(), body, "Email Verification", "EmailVerification");
        }

        log.info("Registration completed successfully for user: {}", request.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username/email: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        log.info("Authentication successful for user: {}", request.getUsername());

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            log.error("User principal is null after authentication");
            throw new NullPointerException("User is getting null");
        }

        log.debug("Validating account status for user ID: {}", user.getId());
        AuthService.validateUserAccountStatus(user);
        log.debug("Account status validation passed");

        log.info("Generating access token for user: {}", user.getUsername());
        String token = authUtils.generateAccessToken(user);
        log.debug("Access token generated successfully");

        AuthResponse response = AuthResponse.builder()
                .username(user.getUsername())
                .token(token)
                .accountStatus(user.getAccountStatus())
                .build();

        log.info("Login successful for user: {}", user.getUsername());
        return response;
    }


    @Transactional
    @Override
    public ResponseEntity<AuthResponse> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        log.info("Handling OAuth2 login for provider: {}", registrationId);

        AuthService.validateOAuthRequest(oAuth2User, registrationId);
        log.debug("OAuth2 request validation passed");

        AuthProviderType providerType = authUtils.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtils.determineProviderIdFromOAuth2User(oAuth2User, registrationId);
        log.debug("Provider type: {}, Provider ID: {}", providerType, providerId);

        String email;
        String fullName;
        String profilePic;
        String username;

        if (registrationId.equalsIgnoreCase("google")) {
            email = oAuth2User.getAttribute("email");
            fullName = oAuth2User.getAttribute("name");
            profilePic = oAuth2User.getAttribute("picture");
            username = email + providerId;
            log.debug("Google OAuth2 attributes - Email: {}, Name: {}", email, fullName);
        } else {
            log.warn("Unsupported OAuth2 provider: {}", registrationId);
            throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        }

        log.info("Checking if user exists by provider ID: {}", providerId);
        User user = userDao.findByProviderId(providerId).orElse(null);

        if (user == null) {
            log.info("Creating new OAuth2 user for provider: {}", registrationId);
            user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(""))
                    .email(email)
                    .fullName(fullName)
                    .profilePic(profilePic)
                    .providerId(providerId)
                    .providerType(providerType.name())
                    .accountStatus(AccountStatus.VERIFIED)
                    .build();
            user = userDao.save(user);
            log.info("New OAuth2 user created with ID: {}", user.getId());
        } else {
            log.info("Existing OAuth2 user found with ID: {}", user.getId());
        }

        log.debug("Generating access token for user ID: {}", user.getId());
        String token = authUtils.generateAccessToken(user);
        log.debug("Access token generated");

        AuthResponse response = AuthResponse.builder()
                .accountStatus(user.getAccountStatus())
                .token(token)
                .username(username)
                .build();

        log.info("OAuth2 login successful for user: {}", username);
        return ResponseEntity.ok(response);
    }


    @Override
    @Transactional
    public void verifyEmail(String email, String verificationToken) {
        log.info("Email verification attempt for email: {}", email);

        User user = userDao.findByEmailAndVerificationToken(email, verificationToken).orElseThrow(
                () -> {
                    log.warn("No user found with email: {} and verification token", email);
                    return new ResourceNotFoundException("No user found with email: " + email + " & verification token: " + verificationToken);
                }
        );

        log.debug("Validating verification token for user ID: {}", user.getId());
        AuthService.validateVerificationToken(
                user.getEmailVerificationToken(),
                user.getEmailVerificationTokenCreatedAt(),
                user.getAccountStatus()
        );
        log.debug("Verification token validation passed");

        log.info("Updating user account status to VERIFIED for user ID: {}", user.getId());
        user.setAccountStatus(AccountStatus.VERIFIED);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenCreatedAt(null);
        user.setEmailVerifiedAt(Instant.now());
        userDao.save(user);

        log.info("Email verification successful for user ID: {}", user.getId());
    }

    @Override
    @Transactional
    public void resendEmailVerification(String email) {
        log.info("Resending email verification for email: {}", email);

        User user = userDao.findByEmail(email).orElseThrow(
                () -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                }
        );

        if (user.getAccountStatus().equals(AccountStatus.VERIFIED) || user.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            log.warn("Verification resend attempted for already verified account: {}", email);
            throw new IllegalArgumentException("Your account is already verified");
        }

        String verificationToken = CommonUtil.generateVerificationToken();
        log.debug("Generated new verification token");
        String verificationLink = CommonUtil.getVerificationLink(baseUrl, user.getEmail(), verificationToken);
        String body = EmailSendingTemplates.sendVerificationEmail(user.getFullName(), verificationLink);
        sendEmailNotification(email, body, "Resend Email Verification", "ResendEmailVerification");
    }

    private void sendEmailNotification(String email, String body, String subject, String eventType) {
        try {
            log.info("Sending verification email to: {}", email);
            emailService.sendEmail(new SendNotificationEvent(email, subject, body, eventType));
            log.info("Verification email sent successfully");
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to send verification email: {}", e.getMessage());
            throw new MailSendException(e.getMessage());
        }
    }

    private void validateEmailAndUsername(String email, String username) {
        log.debug("Validating email and username uniqueness");

        if (userDao.existsByEmail(email)) {
            log.warn("Email already exists: {}", email);
            throw new ExistDataException("Email is already in use");
        }
        if (userDao.existsByUsername(username)) {
            log.warn("Username already exists: {}", username);
            throw new ExistDataException("Username already in use");
        }

        log.debug("Email and username are unique");
    }
}