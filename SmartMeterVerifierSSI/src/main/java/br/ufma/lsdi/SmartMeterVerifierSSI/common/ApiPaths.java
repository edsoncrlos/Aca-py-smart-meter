package br.ufma.lsdi.SmartMeterVerifierSSI.common;

public class ApiPaths {
    public static final String WEBHOOK = "/webhook/topic/{topic}/";
    public static final String CONSUMPTION_BY_HOUR = "/hour-consumption";
    public static final String CONSUMPTION_BY_HOUR_ONE = "/hour-consumption/{uuid}";
    public static final String CREATE_INVITATION = "/create-invitation";

    // Interscity
    public static final String GET_HISTORY_DATA_ALL_RESOURCES = "/resources/data";
    public static final String GET_HISTORY_DATA_ONE_RESOURCE = "/resources/{uuid}/data";
    public static final String GET_RESOURCES_SEARCH = "/resources/search";

    // Secure
    public static final String AUTHENTICATE = "/authenticate";
}
