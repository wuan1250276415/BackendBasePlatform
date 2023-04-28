package pro.wuan.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class UnifiedExceptionHandler {

    /**
     * Handles exceptions of type CustomException and returns an HTTP 400 Bad Request response with the exception message.
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions and returns an HTTP 500 Internal Server Error response with the exception message.
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
