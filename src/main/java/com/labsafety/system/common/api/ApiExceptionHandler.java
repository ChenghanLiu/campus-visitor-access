package com.labsafety.system.common.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("path", null);

        // Optional: include field errors (helpful for debugging)
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Maps common business RuntimeExceptions to meaningful HTTP status codes
     * based on message conventions already used in services.
     *
     * We do this to avoid rewriting all services right now.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() == null ? "Internal server error" : ex.getMessage();

        // 404 conventions
        if (endsWithIgnoreCase(msg, "not found")) {
            return build(HttpStatus.NOT_FOUND, msg);
        }

        // 409 conventions (conflict)
        if (containsIgnoreCase(msg, "conflict")) {
            return build(HttpStatus.CONFLICT, msg);
        }

        // 400 conventions
        if (startsWithIgnoreCase(msg, "invalid") || containsIgnoreCase(msg, "required")) {
            return build(HttpStatus.BAD_REQUEST, msg);
        }

        // fallback: keep as 500 but with message
        return build(HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", null);
        return ResponseEntity.status(status).body(body);
    }

    private boolean containsIgnoreCase(String s, String sub) {
        return s.toLowerCase().contains(sub.toLowerCase());
    }

    private boolean startsWithIgnoreCase(String s, String prefix) {
        return s.toLowerCase().startsWith(prefix.toLowerCase());
    }

    private boolean endsWithIgnoreCase(String s, String suffix) {
        return s.toLowerCase().endsWith(suffix.toLowerCase());
    }
}
