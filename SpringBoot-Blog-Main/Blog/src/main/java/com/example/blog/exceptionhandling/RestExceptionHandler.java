package com.example.blog.exceptionhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //prioritizes this exception class
@ControllerAdvice() //provide exception handling for controllers (helps declutter controllers)
public class RestExceptionHandler{

    //handles errors of @RequestBody if required attributes are missing or null (with @Valid + @NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        //Binding Result interface can provide details of the error
        Map<String, Object> body = new HashMap<>();
        List<String> errors = ex.getBindingResult().getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); //add status code
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase()); //gives error type
        body.put("message", errors); //add error message(s)

        for(String e: errors) {
            log.error("Validation failed. Exception message: {}", e);
        }

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

/*    //handles not found errors (thrown as new Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleNotFound(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        String message = ex.getMessage();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value()); //add status code
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase()); //gives error type
        body.put("message", message); //add error message

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    //handles nullpointer errors
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointer(NullPointerException ex) {
        Map<String, Object> body = new HashMap<>();
        String message = ex.getMessage();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); //add status code
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase()); //gives error type
        body.put("message", message); //add error message

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    //handles duplicate key errors
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex) {
        Map<String, Object> body = new HashMap<>();
        String message = ex.getMessage();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); //add status code
        body.put("error", HttpStatus.CONFLICT.getReasonPhrase()); //gives error type
        body.put("message", message); //add error message

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }*/

}
