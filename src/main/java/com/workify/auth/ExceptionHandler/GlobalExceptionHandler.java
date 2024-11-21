package com.workify.auth.ExceptionHandler;
import com.workify.auth.models.dto.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.regex.PatternSyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(PatternSyntaxException.class)
    public ResponseEntity<ResponseMessage> handlePatternSyntaxException(PatternSyntaxException ex) {
        String errorMessage = "Invalid regex pattern: " + ex.getDescription();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .message(errorMessage)
                .build());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .message(errors.toString())
                .build());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleRuntimeException(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMessage.builder()
                .message(ex.getMessage())
                .build());
    }


    // Catch-all handler for any other exceptions (optional)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMessage.builder()
                .message(ex.getMessage())
                .build());
    }

}
