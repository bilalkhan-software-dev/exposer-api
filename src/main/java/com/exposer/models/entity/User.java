package com.exposer.models.entity;


import com.exposer.models.entity.enums.AccountStatus;
import com.exposer.models.entity.enums.AuthProviderType;
import com.exposer.models.entity.enums.Auth_Role;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User extends AbstractEntity implements UserDetails {

    private String fullName;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String username;
    private String password;

    private String profilePic;

    @Indexed(unique = true)
    @Builder.Default
    private String providerId = UUID.randomUUID().toString();

    @Builder.Default
    private String providerType = AuthProviderType.SELF.name();

    @Builder.Default
    private Auth_Role role = Auth_Role.ROLE_USER;

    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.UNVERIFIED;

    private String emailVerificationToken;
    private LocalDateTime emailVerificationTokenCreatedAt;

    private Instant emailVerifiedAt;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
