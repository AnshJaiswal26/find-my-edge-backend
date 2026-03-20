package com.example.find_my_edge.common.storage.service;

import com.cloudinary.Cloudinary;
import com.example.find_my_edge.common.storage.dto.ImageUploadResponse;
import com.example.find_my_edge.common.storage.exception.ImageDeletionFailed;
import com.example.find_my_edge.common.storage.exception.ImageUploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final Cloudinary cloudinary;

    public ImageUploadResponse upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "trade-setups")
            );

            String secureUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

           return new ImageUploadResponse(secureUrl, publicId);

        } catch (IOException e) {
            throw new ImageUploadFailedException("Image upload failed");
        }
    }

    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException e) {
            throw new ImageDeletionFailed("Image deletion failed");
        }
    }
}