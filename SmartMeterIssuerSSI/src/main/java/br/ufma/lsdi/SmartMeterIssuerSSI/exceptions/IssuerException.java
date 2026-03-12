package br.ufma.lsdi.SmartMeterIssuerSSI.exceptions;

public class IssuerException extends RuntimeException {
    public IssuerException(String message) {
        super(message);
    }

    public IssuerException(String message, Throwable cause) {
        super(message, cause);
    }
}
