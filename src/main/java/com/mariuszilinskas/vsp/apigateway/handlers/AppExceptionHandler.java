package com.mariuszilinskas.vsp.apigateway.handlers;

import com.mariuszilinskas.vsp.apigateway.dto.ErrorResponse;
import com.mariuszilinskas.vsp.apigateway.dto.FieldErrorResponse;
import com.mariuszilinskas.vsp.apigateway.exception.JwtTokenValidationException;
import com.mariuszilinskas.vsp.apigateway.exception.NoAccessException;
import com.mariuszilinskas.vsp.apigateway.exception.ResourceNotFoundException;
import com.mariuszilinskas.vsp.apigateway.exception.SessionExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

/**
 * This class is a global exception handler that handles exceptions thrown across the whole application.
 *
 * @author Marius Zilinskas
 */
@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    /**
     * Exception for Request Validations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        FieldErrorResponse errorResponse = new FieldErrorResponse(errors, status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }

    // --------------------- General ------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // --------------------- Specific -----------------------------

    @ExceptionHandler(JwtTokenValidationException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenValidationException(JwtTokenValidationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<ErrorResponse> handleNoAccessException(NoAccessException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpiredException(SessionExpiredException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // -----------------------------------------------------------

    /**
     * This method builds the error response for a given exception.
     *
     * @param message the exception message
     * @param status the HTTP status
     * @return a ResponseEntity that includes the error response and the given HTTP status
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        logger.error("Status: {}, Message: '{}'", status.value(), message);
        ErrorResponse errorResponse = new ErrorResponse(message, status.value(), status.getReasonPhrase());
        return new ResponseEntity<>(errorResponse, status);
    }
}
