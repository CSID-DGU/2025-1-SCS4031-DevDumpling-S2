package com.example.controller;

import com.example.ml.ModelRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/recommend")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final ModelRunner modelRunner;

    @Autowired
    public RecommendationController(ModelRunner modelRunner) {
        this.modelRunner = modelRunner;
    }

    @PostMapping
    public ResponseEntity<?> getRecommendations(@RequestBody Map<String, Object> userData) {
        try {
            log.info("Received recommendation request with data: {}", userData);
            List<Map<String, Object>> recommendations = modelRunner.getRecommendations(userData);
            log.info("Generated recommendations: {}", recommendations);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error generating recommendations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "추천 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 