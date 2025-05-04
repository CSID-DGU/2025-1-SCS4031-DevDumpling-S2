package com.example.dto.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryInfoResponse {
    private String name;      // 카테고리 이름 (displayName)
    private String imageUrl;  // S3 이미지 URL
} 