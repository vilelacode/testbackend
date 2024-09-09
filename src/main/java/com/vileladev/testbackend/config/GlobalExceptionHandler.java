package com.vileladev.testbackend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.UnexpectedTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors
                .put("Erro em " +
                        error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();

        Pattern pattern = Pattern.compile("Cannot (.*) from String \"(.*)\"");
        Matcher matcher = pattern.matcher(ex.getMostSpecificCause().getMessage());

        if(matcher.find()){
            error.put("Valor inserido inválido", matcher.group(2));
        } else {
            error.put("Valor inserido não interpretável", ex.getMostSpecificCause().getMessage());
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedTypeException(UnexpectedTypeException ex) {
        Map<String, String> error = new HashMap<>();

        Pattern pattern = Pattern.compile("Unrecognized token \\s'([^']+)':");
        Matcher matcher = pattern.matcher(ex.getLocalizedMessage());

        if (matcher.find()) {
            error.put("Valor inserido inválido", matcher.group(1));
        } else {
            error.put("Valor inserido não interpretável", ex.getLocalizedMessage());
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}


