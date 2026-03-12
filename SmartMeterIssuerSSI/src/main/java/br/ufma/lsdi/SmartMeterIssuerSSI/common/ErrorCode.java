package br.ufma.lsdi.SmartMeterIssuerSSI.common;

public enum ErrorCode {
    USER_NO_PERMISSION("User don't have permission"),
    AGENT_COMMUNICATION_ERROR("Error communication with the agent"),
    AGENT_RESPONSE_ERROR("Error agent response");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
