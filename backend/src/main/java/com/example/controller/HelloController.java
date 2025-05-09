package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class HelloController {

    @GetMapping("/")
    public String root() {
        return "Hello World from Root";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
} 