package br.ufma.lsdi.SmartMeterIssuerSSI.security;

public enum Role {
    EMPLOYEE,
    USER;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
