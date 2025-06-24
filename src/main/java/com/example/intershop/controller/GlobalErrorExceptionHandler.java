package com.example.intershop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ProblemDetail> handleThrowable(Throwable ex) {
        log.error("Throwable occurred: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
}