package com.checkmarx.integrations.datastore.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Object> handleTokenNotFoundException(TokenNotFoundException e) {

        GeneralExceptionDO generalExceptionDO = GeneralExceptionDO.builder()
                .message(e.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(generalExceptionDO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RepoNotFoundException.class)
    public ResponseEntity<Object> handleReponNotFoundException(RepoNotFoundException e) {

        GeneralExceptionDO generalExceptionDO = GeneralExceptionDO.builder()
                .message(e.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(generalExceptionDO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler()
    public ResponseEntity<Object> handleRuntimeRequestException(RuntimeException e){
        log.error("Runtime Exception: ", e);
        GeneralExceptionDO generalExceptionDO = GeneralExceptionDO.builder()
                .message(e.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(generalExceptionDO, HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Malformed JSON request: ", ex.getMessage());
        GeneralExceptionDO generalExceptionDO = GeneralExceptionDO.builder()
                .message(ex.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(generalExceptionDO, HttpStatus.BAD_REQUEST);
    }
}