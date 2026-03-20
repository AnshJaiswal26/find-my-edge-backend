package com.example.find_my_edge.common.storage.controller;

import com.example.find_my_edge.common.controller.BaseController;
import com.example.find_my_edge.common.dto.ApiResponse;
import com.example.find_my_edge.common.storage.dto.ImageUploadResponse;
import com.example.find_my_edge.common.storage.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController extends BaseController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        ImageUploadResponse upload = imageStorageService.upload(file);

        return buildResponse(upload, "Image uploaded successfully");
    }
}