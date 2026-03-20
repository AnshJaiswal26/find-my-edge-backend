package com.example.find_my_edge.common.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageUploadResponse {
    private String imageUrl;
    private String publicId;
}
