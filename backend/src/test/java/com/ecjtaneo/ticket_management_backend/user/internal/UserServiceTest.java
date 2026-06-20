package com.ecjtaneo.ticket_management_backend.user.internal;

import com.ecjtaneo.ticket_management_backend.shared.dtos.MessageResponse;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.storage.StorageApi;
import com.ecjtaneo.ticket_management_backend.user.UserBasicInfo;
import com.ecjtaneo.ticket_management_backend.user.internal.dto.UserInfoResponseDto;
import com.ecjtaneo.ticket_management_backend.user.internal.mapper.UserMapper;
import com.ecjtaneo.ticket_management_backend.user.internal.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private StorageApi storageApi;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(repository, mapper, storageApi);
    }

    @Test
    void getUserInfoById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("testUser");

        UserInfoResponseDto expectedDto = new UserInfoResponseDto(userId, "testUser", "CUSTOMER", null, LocalDateTime.now());

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toUserInfoResponseDto(user)).thenReturn(expectedDto);

        UserInfoResponseDto actualDto = userService.getUserInfo(userId);

        assertThat(actualDto).isEqualTo(expectedDto);
        verify(repository).findById(userId);
        verify(mapper).toUserInfoResponseDto(user);
    }

    @Test
    void getUserInfoById_UserNotFound_ThrowsException() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfo(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(repository).findById(userId);
        verifyNoInteractions(mapper);
    }

    @Test
    void getUserInfoByName_Success() {
        String name = "testUser";
        User user = new User();
        user.setId(1L);
        user.setName(name);

        UserBasicInfo expectedInfo = new UserBasicInfo(1L, name, "CUSTOMER", "hashedPassword");

        when(repository.findByName(name)).thenReturn(Optional.of(user));
        when(mapper.toBasicInfo(user)).thenReturn(expectedInfo);

        UserBasicInfo actualInfo = userService.getUserInfo(name);

        assertThat(actualInfo).isEqualTo(expectedInfo);
        verify(repository).findByName(name);
        verify(mapper).toBasicInfo(user);
    }

    @Test
    void getUserInfoByName_UserNotFound_ThrowsException() {
        String name = "nonExistentUser";
        when(repository.findByName(name)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfo(name))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(repository).findByName(name);
        verifyNoInteractions(mapper);
    }

    @Test
    void changeProfilePhoto_Success() throws IOException {
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        User user = new User();
        user.setId(userId);
        user.setName("testUser");

        String expectedUrl = "http://cloudinary.com/profile.jpg";

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(storageApi.uploadUserProfilePhoto(mockFile, userId)).thenReturn(expectedUrl);

        MessageResponse response = userService.changeProfilePhoto(mockFile, userId);

        assertThat(response.message()).isEqualTo("Profile updated successfully");
        assertThat(user.getProfileImageUrl()).isEqualTo(expectedUrl);

        verify(repository).findById(userId);
        verify(storageApi).uploadUserProfilePhoto(mockFile, userId);
        verify(repository).save(user);
    }

    @Test
    void changeProfilePhoto_UserNotFound_ThrowsException() {
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changeProfilePhoto(mockFile, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(repository).findById(userId);
        verifyNoInteractions(storageApi);
    }
}
