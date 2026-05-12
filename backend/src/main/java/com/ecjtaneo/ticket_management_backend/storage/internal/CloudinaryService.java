package com.ecjtaneo.ticket_management_backend.storage.internal;

import com.cloudinary.Cloudinary;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.ecjtaneo.ticket_management_backend.storage.StorageApi;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService implements StorageApi {
    private final Cloudinary cloudinary;
    private final Tika tika = new Tika();
    private final List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/webp");

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public void validateFile(MultipartFile file) throws IOException {
        String detectedMimeType = tika.detect(file.getInputStream());
        if (!allowedMimeTypes.contains(detectedMimeType)) {
            throw new ValidationException("Invalid file type.");
        }
    }

    @Override
    public String uploadUserProfilePhoto(MultipartFile file, Long userId) throws IOException {
        this.validateFile(file);
        Map<String, Object> options = Map.of(
                "folder", "uploads/profiles",
                "public_id", "user_" + userId,
                "overwrite", true);
        return cloudinary.uploader().uploadLarge(file.getInputStream(), options)
                .get("secure_url").toString();
    }

    @Override
    public String uploadEventPhoto(MultipartFile file, Long eventId) throws IOException {
        this.validateFile(file);
        Map<String, Object> options = Map.of(
                "folder", "uploads/events",
                "public_id", "event_" + eventId,
                "overwrite", true);
        return cloudinary.uploader().uploadLarge(file.getInputStream(), options)
                .get("secure_url").toString();
    }

}
