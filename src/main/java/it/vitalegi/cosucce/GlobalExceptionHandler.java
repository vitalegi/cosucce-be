package it.vitalegi.cosucce;

import it.vitalegi.cosucce.security.exception.UnauthorizedBoardAccessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(UnauthorizedBoardAccessException.class)
    public ResponseEntity<GenericErrorResponse> handleUnauthorizedBoardAccessException(UnauthorizedBoardAccessException ex, HttpServletRequest request) {
        var error = new GenericErrorResponse(Instant.now(), ex.getClass().getSimpleName(), ex.getMessage());
        log.info("Forbidden access on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GenericErrorResponse> handleGenericException(DataIntegrityViolationException ex, HttpServletRequest request) {
        var error = new GenericErrorResponse(Instant.now(), ex.getClass().getSimpleName(), "Invalid argument provided");
        log.error("Error on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
