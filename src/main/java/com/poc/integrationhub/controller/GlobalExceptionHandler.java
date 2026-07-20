package com.poc.integrationhub.controller;

import com.poc.integrationhub.exception.ConnectorInactiveException;
import com.poc.integrationhub.exception.ConnectorNotFoundException;
import com.poc.integrationhub.exception.N8nCallFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ConnectorNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "CONNECTOR_NOT_FOUND", "message", e.getMessage()));
    }

    @ExceptionHandler(ConnectorInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleInactive(ConnectorInactiveException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "CONNECTOR_INACTIVE", "message", e.getMessage()));
    }

    @ExceptionHandler(N8nCallFailedException.class)
    public ResponseEntity<Map<String, Object>> handleN8nFailed(N8nCallFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "N8N_FAILED", "message", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "INTERNAL_ERROR", "message", e.getMessage() != null ? e.getMessage() : "Erreur interne"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "UNEXPECTED_ERROR", "message", e.getMessage() != null ? e.getMessage() : "Erreur inattendue"));
    }
}
