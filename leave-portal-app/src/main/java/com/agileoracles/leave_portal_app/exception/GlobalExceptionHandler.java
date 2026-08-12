package com.agileoracles.leave_portal_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFile(
            InvalidFileException exception
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, Object>> handleMissingFile(
            MissingServletRequestPartException exception
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "The multipart field named 'file' is required."
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleLargeFile(
            MaxUploadSizeExceededException exception
    ) {
        return buildError(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file exceeds the allowed size."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(
            Exception exception
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "The leave request could not be processed."
        );
    }

    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status,
            String message
    ) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", Instant.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}