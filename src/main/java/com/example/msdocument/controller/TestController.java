package com.example.msdocument.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("Hi, " + jwt.getClaimAsString("preferred_username"));
    }
}