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
class UserController {
    private final UserService service;

    @GetMapping("/me")
    UserInfoResponseDto getUserInfo(@CurrentUserId Long userId) {
        return service.getUserInfo(userId);
    }

    @PatchMapping("/me/profile-photo")
    MessageResponseDto changeProfilePhoto(@RequestParam("file") MultipartFile file, @CurrentUserId Long userId) throws IOException {
        return service.changeProfilePhoto(file, userId);
    }
}
