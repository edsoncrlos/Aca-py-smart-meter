package br.ufma.lsdi.SmartMeterVerifierSSI.configs;

import org.hyperledger.aries.AriesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AriesClientConfig {
    private final String ariesAgentUrl;

    public AriesClientConfig(@Value("${aries.agent.url}") String ariesAgentUrl) {
        this.ariesAgentUrl = ariesAgentUrl;
    }

    @Bean(destroyMethod = "")
    public AriesClient ariesAgentClient() {
        var e =  AriesClient.builder()
                .url(ariesAgentUrl)
                .build();
        return  e;
    }
}
