package br.ufma.lsdi.SmartMeterIssuerSSI.configs;

import org.hyperledger.aries.AriesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AriesClientConfig {
    private final String ariesAgentUrl;
    private final String apiKey;

    public AriesClientConfig(
            @Value("${aries.agent.url}") String ariesAgentUrl,
            @Value("${aries.agent.api-key}") String apiKey
    ) {
        this.ariesAgentUrl = ariesAgentUrl;
        this.apiKey = apiKey;
    }

    @Bean(destroyMethod = "")
    public AriesClient ariesAgentClient() {
        return AriesClient.builder()
                .url(ariesAgentUrl)
                .apiKey(apiKey)
                .build();
    }
}
