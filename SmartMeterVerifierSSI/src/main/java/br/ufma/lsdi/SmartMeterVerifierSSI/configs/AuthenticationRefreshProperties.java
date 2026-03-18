package br.ufma.lsdi.SmartMeterVerifierSSI.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.refresh")
public class AuthenticationRefreshProperties {
    @Value("${auth.refresh.long}")
    private Long longTime;
    @Value("${auth.refresh.short}")
    private Long shortime;

    public Long getLongTime() {
        return longTime*60*1000L;
    }

    public Long getShortime() {
        return shortime*60*1000L;
    }
}
