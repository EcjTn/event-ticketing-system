package com.ecjtaneo.ticket_management_backend.infrastructure.security;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.user.UserApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserApi userApi;
    
    @Override
    public UserDetailsImpl loadUserByUsername(String username) {
        UserBasicInfo userBasicInfo = userApi.getUserBasicInfo(username);
        return new UserDetailsImpl(userBasicInfo);
    }
}
