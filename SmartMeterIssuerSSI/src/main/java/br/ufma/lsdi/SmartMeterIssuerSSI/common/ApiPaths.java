package br.ufma.lsdi.SmartMeterIssuerSSI.common;

public final class ApiPaths {
    public static final String WEBHOOK_SECURE = "/webhook/topic/**";
    public static final String WEBHOOK = "/webhook/topic/{topic}";

    public static final String CREATE_INVITATION = "/create-invitation";
}
