package com.ecjtaneo.ticket_management_backend.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    public String uploadUserProfilePhoto(MultipartFile file, Long userId) throws IOException;
    public String uploadEventPhoto(MultipartFile file, Long eventId) throws IOException;
}
