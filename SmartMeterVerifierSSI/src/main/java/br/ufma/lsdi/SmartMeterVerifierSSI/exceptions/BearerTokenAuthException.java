package br.ufma.lsdi.SmartMeterVerifierSSI.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BearerTokenAuthException extends RuntimeException {
    @Getter
    private HttpStatus httpStatus;

    public BearerTokenAuthException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
