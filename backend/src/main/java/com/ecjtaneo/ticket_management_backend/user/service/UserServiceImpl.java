package com.ecjtaneo.ticket_management_backend.user.service;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecjtaneo.ticket_management_backend.shared.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.storage.StorageService;
import com.ecjtaneo.ticket_management_backend.user.UserService;
import com.ecjtaneo.ticket_management_backend.user.dto.UserInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.user.mapper.UserMapper;
import com.ecjtaneo.ticket_management_backend.user.model.User;
import com.ecjtaneo.ticket_management_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final StorageService storageService;

    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = repository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toUserInfoResponseDto(user);
    }

    @Override
    public UserBasicInfo getUserBasicInfo(String name) {
        User user = repository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toBasicInfo(user);
    }

    public MessageResponseDto changeProfilePhoto(MultipartFile file, Long userId) throws IOException {
        User user = repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String fileUrl = storageService.uploadUserProfilePhoto(file, userId);

        user.setProfileImageUrl(fileUrl);
        repository.save(user);

        return new MessageResponseDto("Profile updated successfully");
    }
    
}
