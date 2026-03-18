package br.ufma.lsdi.SmartMeterVerifierSSI.exceptions;

public class InvitationException extends RuntimeException {
    public InvitationException(String message) {
        super(message);
    }

    public InvitationException(String message, Throwable cause) {
        super(message, cause);
    }
}

