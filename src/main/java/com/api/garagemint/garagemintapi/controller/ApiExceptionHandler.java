package com.api.garagemint.garagemintapi.controller;

import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

  // Hata cevabı sözleşmesi
  public record ApiError(Instant timestamp, int status, String error, String message, Map<String, String> fields) { }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    log.error("NotFoundException: {}", ex.getMessage(), ex);
    return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), null);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiError> handleValidation(ValidationException ex) {
    log.error("ValidationException: {}", ex.getMessage(), ex);
    return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), null);
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ApiError> handleBusiness(BusinessRuleException ex) {
    log.error("BusinessRuleException: {}", ex.getMessage(), ex);
    return build(HttpStatus.CONFLICT, "BUSINESS_RULE_VIOLATION", ex.getMessage(), null);
  }

  // Bean Validation (@Valid) alan hataları
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleBeanValidation(MethodArgumentNotValidException ex) {
    log.error("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
    Map<String,String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
    return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid request", fields);
  }

  // En sonda genel yakalayıcı
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleOther(Exception ex) {
    log.error("Exception: {}", ex.getMessage(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), null);
  }

  private ResponseEntity<ApiError> build(HttpStatus s, String code, String msg, Map<String,String> fields) {
    return ResponseEntity.status(s)
        .body(new ApiError(Instant.now(), s.value(), code, msg, fields));
  }
}
