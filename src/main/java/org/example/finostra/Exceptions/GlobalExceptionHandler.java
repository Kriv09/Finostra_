package org.example.finostra.Exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {

        var statusCode = HttpStatus.NOT_FOUND;
        String message = e.getMessage();
        Date timestamp = new Date();

        ApiError apiError = new ApiError(
                statusCode.value(),
                message,
                timestamp
        );

        return new ResponseEntity<ApiError>(apiError, statusCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {

        var statusCode = HttpStatus.BAD_REQUEST;
        String message = e.getMessage();
        Date timestamp = new Date();

        ApiError apiError = new ApiError(
                statusCode.value(),
                message,
                timestamp
        );

        return new ResponseEntity<ApiError>(apiError, statusCode);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        StringBuilder msgBuilder = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            msgBuilder.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        var statusCode = HttpStatus.BAD_REQUEST;
        String message = msgBuilder.toString();
        Date timestamp = new Date();

        ApiError apiError = new ApiError(
                statusCode.value(),
                message,
                timestamp
        );

        return new ResponseEntity<ApiError>(apiError, statusCode);
    }

}
