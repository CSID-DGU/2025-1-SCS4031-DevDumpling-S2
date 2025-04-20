package com.example.controller;

import com.example.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = s3Service.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }
} 