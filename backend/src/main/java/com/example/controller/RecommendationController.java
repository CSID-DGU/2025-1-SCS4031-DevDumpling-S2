package com.example.controller;

import com.example.ml.ModelRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final ModelRunner modelRunner;

    @Autowired
    public RecommendationController(ModelRunner modelRunner) {
        this.modelRunner = modelRunner;
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(@RequestBody Map<String, Object> userData) {
        try {
            List<Map<String, Object>> recommendations = modelRunner.getRecommendations(userData);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 