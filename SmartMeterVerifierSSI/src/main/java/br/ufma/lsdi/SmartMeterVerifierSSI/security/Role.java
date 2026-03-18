package br.ufma.lsdi.SmartMeterVerifierSSI.security;

public enum Role {
    EMPLOYEE,
    USER;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}