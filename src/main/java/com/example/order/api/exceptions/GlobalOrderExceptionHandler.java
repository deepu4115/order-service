package com.example.order.api.exceptions;

import com.example.order.shared.ErrorStructure;
import com.example.order.shared.RestResponseBuilder;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalOrderExceptionHandler {

    private final RestResponseBuilder responseBuilder;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorStructure> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return responseBuilder.error(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorStructure> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return responseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorStructure> handleFeignException(FeignException ex) {
        log.error("Feign client communication error: status {} message {}", ex.status(), ex.getMessage());
        HttpStatus status = ex.status() > 0 ? HttpStatus.valueOf(ex.status()) : HttpStatus.INTERNAL_SERVER_ERROR;
        return responseBuilder.error(status, "Downstream service error: " + ex.getMessage());
    }
}
