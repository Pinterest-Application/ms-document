package com.example.msdocument.controller;

import com.example.msdocument.dto.PreSignedUrlRequest;
import com.example.msdocument.dto.PreSignedUrlResponse;
import com.example.msdocument.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<PreSignedUrlResponse> generateUrl(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PreSignedUrlRequest preSignedUrlRequest) {

        return ResponseEntity.ok(documentService.generateUrl(jwt.getSubject(), preSignedUrlRequest));
    }

}
