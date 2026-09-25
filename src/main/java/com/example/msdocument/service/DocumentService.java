package com.example.msdocument.service;

import com.example.libexception.exception.InternalServerErrorException;
import com.example.msdocument.dto.PreSignedDownloadUrlRequest;
import com.example.msdocument.dto.PreSignedDownloadUrlResponse;
import com.example.msdocument.dto.PreSignedUrlRequest;
import com.example.msdocument.dto.PreSignedUrlResponse;
import com.example.msdocument.exception.UnsupportedFileFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
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

    public PreSignedDownloadUrlResponse downloadUrl(PreSignedDownloadUrlRequest request) {

        GetObjectRequest.Builder getObjectRequest = GetObjectRequest.builder()
                .bucket(request.getBucketType().getBucketName())
                .key(request.getObjectKey());

        if (request.getDownloadFileName() != null && !request.getDownloadFileName().isBlank()) {
            getObjectRequest.responseContentDisposition(
                    "attachment; filename=\"" + request.getDownloadFileName() + "\"");
        }

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest.build())
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return PreSignedDownloadUrlResponse.builder()
                .preSignedDownloadUrl(presignedGetObjectRequest.url().toString())
                .build();
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

    private String generateObjectKey(String userId, String contentType) {
        String ext = getExtensionFromContentType(contentType);
        String userFolder = hashString(userId).substring(0, 16);

        String fileHash = UUID.randomUUID().toString().replace("-", "");
        String dir1 = fileHash.substring(0, 2);
        String dir2 = fileHash.substring(2, 4);
        String dir3 = fileHash.substring(4, 6);

        return String.format("%s/%s/%s/%s/%s%s", userFolder, dir1, dir2, dir3, fileHash, ext);
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("Hashing algorithm not supported");
        }
    }

    // todo: think content type and filename ext (equals)

}
