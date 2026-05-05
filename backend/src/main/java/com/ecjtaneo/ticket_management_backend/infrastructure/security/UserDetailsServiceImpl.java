package com.ecjtaneo.ticket_management_backend.infrastructure.security;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.ecjtaneo.ticket_management_backend.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    
    @Override
    public UserDetailsImpl loadUserByUsername(String username) {
        UserBasicInfo userBasicInfo = userService.getUserBasicInfo(username);
        return new UserDetailsImpl(userBasicInfo);
    }
}
