package it.vitalegi.cosucce;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        var error = new GenericErrorResponse(Instant.now(), ex.getClass().getSimpleName(), ex.getMessage());
        log.error("Error on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
