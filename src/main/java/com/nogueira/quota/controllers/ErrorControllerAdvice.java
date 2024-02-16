package com.nogueira.quota.controllers;

import com.nogueira.quota.exceptions.QuotaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorControllerAdvice.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handle(NoResourceFoundException e) {
        LOGGER.error("not found", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        LOGGER.error("unknown exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("ops");
    }

    @ExceptionHandler(QuotaException.class)
    public ResponseEntity<String> handle(QuotaException e) {
        LOGGER.error("QuotaError", e);
        return ResponseEntity
                .status(e.getErrorCode())
                .body(e.getMessage());
    }
}
