package com.ecjtaneo.ticket_management_backend.user.internal;

import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.storage.StorageApi;
import com.ecjtaneo.ticket_management_backend.user.UserApi;
import com.ecjtaneo.ticket_management_backend.user.internal.dto.UserInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.user.internal.mapper.UserMapper;
import com.ecjtaneo.ticket_management_backend.user.internal.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserApi {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final StorageApi storageApi;

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

        String fileUrl = storageApi.uploadUserProfilePhoto(file, userId);

        user.setProfileImageUrl(fileUrl);
        repository.save(user);

        return new MessageResponseDto("Profile updated successfully");
    }

}
