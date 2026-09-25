package com.example.msdocument.exception;

import com.example.libexception.exception.BadRequestException;
import com.example.msdocument.exception.error.DocumentErrorCode;

public class UnsupportedFileFormatException extends BadRequestException {
    public UnsupportedFileFormatException() {
        super(DocumentErrorCode.UNSUPPORTED_FILE_FORMAT);
    }
}
