package hr.rba.interview.advice;

import hr.rba.interview.exception.FileException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalAdviceController {

  @ExceptionHandler({
      NoSuchElementException.class,
      EntityNotFoundException.class
  })
  public ResponseEntity<String> handleNotFoundException(final RuntimeException e) {
    log.error("Caught exception.", e);
    return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
      HttpClientErrorException.class,
      FileException.class,
      DataIntegrityViolationException.class,
      IllegalArgumentException.class,
      IllegalStateException.class,
      ValidationException.class
  })
  public ResponseEntity<String> handleBadRequestException(final RuntimeException e) {
    log.error("Caught exception.", e);
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
    log.error(e.getMessage());
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<String> handleOtherException(final RuntimeException e) {
    log.error("Caught exception.", e);
    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
