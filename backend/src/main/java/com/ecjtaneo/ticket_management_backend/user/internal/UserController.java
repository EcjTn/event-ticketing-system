package com.ecjtaneo.ticket_management_backend.user.internal;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecjtaneo.ticket_management_backend.shared.annotations.CurrentUserId;
import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponseDto;
import com.ecjtaneo.ticket_management_backend.user.internal.dto.UserInfoResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl service;

    @GetMapping("/me")
    public UserInfoResponseDto getUserInfo(@CurrentUserId Long userId) {
        return service.getUserInfo(userId);
    }

    @PatchMapping("/me/profile-photo")
    public MessageResponseDto changeProfilePhoto(@RequestParam("file") MultipartFile file, @CurrentUserId Long userId) throws IOException {
        return service.changeProfilePhoto(file, userId);
    }
}
