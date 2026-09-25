package com.example.msdocument.service;

import com.example.msdocument.dto.PreSignedUrlRequest;
import com.example.msdocument.dto.PreSignedUrlResponse;
import com.example.msdocument.exception.UnsupportedFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final S3Presigner s3Presigner;

    public PreSignedUrlResponse generateUrl(String userId, PreSignedUrlRequest request) {
        if (!ALLOWED_IMAGE_TYPES.contains(request.getContentType())) {
            throw new UnsupportedFileFormatException();
        }

        String objectKey = generateObjectKey(userId, request.getContentType());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(request.getBucketType().getBucketName())
                .key(objectKey)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);
        return PreSignedUrlResponse.builder()
                .preSignedUrl(presignedPutObjectRequest.url().toString())
                .objectKey(objectKey)
                .build();
    }


    private String generateObjectKey(String userId, String contentType) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = getExtensionFromContentType(contentType);
        String uniqueFileName = UUID.randomUUID() + ext;
        return String.format("%s/%s/%s", userId, datePath, uniqueFileName);

        //todo currently basic object key, should be advanced
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> throw new UnsupportedFileFormatException();
        };
    }

    // todo: think content type and filename ext (equals)

}
