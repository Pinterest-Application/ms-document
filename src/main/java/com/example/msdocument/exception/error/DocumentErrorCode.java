package com.example.msdocument.exception.error;

import com.example.libexception.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DocumentErrorCode implements ErrorCode {

    UNSUPPORTED_FILE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Yalnız JPEG, PNG və WEBP formatları qəbul edilir."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Fayl yaddaşa saxlanılarkən xəta baş verdi."),
    ;



    private final HttpStatus httpStatus;
    private final String defaultMessage;

    DocumentErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getDefaultMessage() {
        return this.defaultMessage;
    }
    }