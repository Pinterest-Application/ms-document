package com.example.msdocument.dto;


import com.example.msdocument.dto.enums.BucketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreSignedUrlRequest {
    @NotNull(message = "bucket type is not null")
    private BucketType bucketType;

    @NotBlank(message = "content type is not blank")
    private String contentType;
    private String fileName;
}
