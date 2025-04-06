package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 프론트엔드를 위한 CORS 설정
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
} 