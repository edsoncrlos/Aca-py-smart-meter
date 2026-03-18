package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.configs.AuthenticationRefreshProperties;
import br.ufma.lsdi.SmartMeterVerifierSSI.utils.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Component
public class AuthenticationFactory {
    private final String serverIdentifier;
    private final Signature signature;

    private final AuthenticationRefreshProperties refreshProperties;
    private final TaskScheduler scheduler;

    public AuthenticationFactory(
        @Value("${server.identifier}") String serverIdentifier,
        AuthenticationRefreshProperties refreshProperties,
        TaskScheduler scheduler,
        Signature signature
    ) {
        this.serverIdentifier = serverIdentifier;
        this.refreshProperties = refreshProperties;
        this.scheduler = scheduler;
        this.signature = signature;
    }

    public AuthenticationService create(RestClient restClient) {

        return new AuthenticationService(
                restClient,
                serverIdentifier,
                refreshProperties,
                scheduler,
                signature
        );
    }
}
