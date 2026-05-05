package com.ecjtaneo.ticket_management_backend.infrastructure.security;

import java.util.Collection;
import java.util.List;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final Long userId;
    private final String name;
    private final String password;
    private final String role;

    public UserDetailsImpl(UserBasicInfo user) {
        this.userId = user.id();
        this.name = user.name();
        this.password = user.password();
        this.role = user.role();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
