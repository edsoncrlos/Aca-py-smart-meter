package br.ufma.lsdi.SmartMeterVerifierSSI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BearerTokenAuthException.class)
    public ResponseEntity<String> BearerTokenAuthException(BearerTokenAuthException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The request could not be completed. Please retry.");
    }

    @ExceptionHandler(NumberResourcesException.class)
    public ResponseEntity<String> NumberResourcesException(NumberResourcesException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The number of resources in the search range must be greater than or equal to 3.");
    }
}
