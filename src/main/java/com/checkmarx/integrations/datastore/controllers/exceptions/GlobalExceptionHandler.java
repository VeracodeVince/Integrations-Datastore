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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GeneralExceptionDO> handleRepoNotFoundException(EntityNotFoundException e) {
        // There is no need to log a huge stack trace.
        log.error("Intercepted {}", e.toString());

        GeneralExceptionDO externalException = toExternalException(e);
        return new ResponseEntity<>(externalException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DuplicateKeyException.class, DataStoreException.class})
    public ResponseEntity<GeneralExceptionDO> handleBadRequest(RuntimeException e) {
        log.error("Intercepted {}", e.toString());
        return new ResponseEntity<>(toExternalException(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler()
    public ResponseEntity<GeneralExceptionDO> handleRuntimeRequestException(RuntimeException e) {
        log.error("Runtime Exception: ", e);
        GeneralExceptionDO externalException = toExternalException(e);
        return new ResponseEntity<>(externalException, HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Malformed JSON request: ", ex.getCause());
        GeneralExceptionDO externalException = toExternalException(ex);
        return new ResponseEntity<>(externalException, HttpStatus.BAD_REQUEST);
    }

    private GeneralExceptionDO toExternalException(RuntimeException cause) {
        return GeneralExceptionDO.builder()
                .message(cause.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();
    }
}