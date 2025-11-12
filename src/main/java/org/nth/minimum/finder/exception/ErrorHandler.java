package org.nth.minimum.finder.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(IOException.class)
  public ResponseEntity<Error> handleIOException(IOException e) {
    return ResponseEntity.badRequest().body(new Error(e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Error> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(new Error(e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<Error>> handleException(MethodArgumentNotValidException e) {
    List<Error> result = new ArrayList<>();
    for (var error : e.getAllErrors()) {
      result.add(new Error(error.getDefaultMessage()));
    }

    return ResponseEntity.badRequest().body(result);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handleException(Exception e) {
    return ResponseEntity.internalServerError().body(new Error(e.getMessage()));
  }
}
