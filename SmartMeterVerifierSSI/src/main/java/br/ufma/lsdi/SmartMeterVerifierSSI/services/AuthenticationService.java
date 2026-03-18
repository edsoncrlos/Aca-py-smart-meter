package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.TokenRequestDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.TokenResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterVerifierSSI.configs.AuthenticationRefreshProperties;
import br.ufma.lsdi.SmartMeterVerifierSSI.exceptions.BearerTokenAuthException;
import br.ufma.lsdi.SmartMeterVerifierSSI.utils.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final RestClient restClient;
    private final String serverIdentifier;
    private final Signature signature;

    private final AuthenticationRefreshProperties refreshProperties;
    private final TaskScheduler scheduler;
    private final AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

    private volatile String token;

    public AuthenticationService(
            RestClient restClient,
            String serverIdentifier,
            AuthenticationRefreshProperties refreshProperties,
            TaskScheduler scheduler,
            Signature signature
    ) {
        this.restClient = restClient;
        this.serverIdentifier = serverIdentifier;
        this.refreshProperties = refreshProperties;
        this.scheduler = scheduler;
        this.signature = signature;

        nextSchedule(0);
    }

    public String getToken() {
        if (token == null) {
            throw new BearerTokenAuthException("Token not initialized yet", HttpStatus.BAD_GATEWAY);
        }

        return token;
    }

    private void authenticate() {
        TokenRequestDTO tokenRequestDTO = new TokenRequestDTO(serverIdentifier, signature.getSignature());

        TokenResponseDTO response = restClient.post()
                .uri(ApiPaths.AUTHENTICATE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tokenRequestDTO)
                .retrieve()
                .body(TokenResponseDTO.class);

        if (response == null || response.token() == null) {
            throw new IllegalStateException("Invalid authentication response");
        }
        this.token = response.token();
        log.info("Authentication successful");
    }

    private void handleNewToken() {
        try {
            authenticate();
            nextSchedule(refreshProperties.getLongTime());

        } catch (HttpClientErrorException e) {
            log.error("Client error during authentication: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            // TODO: Contact admins

        } catch (HttpServerErrorException e) {
            log.warn("Server error during authentication. Will retry in {} ms",
                    refreshProperties.getShortime(), e);

            nextSchedule(refreshProperties.getShortime());
        } catch (RestClientException e) {
            // TODO: Contact admins

            throw new RuntimeException(e);
        }
    }

    private void nextSchedule(long delayMillis) {
        log.debug("Scheduling next refresh in {} ms", delayMillis);

        ScheduledFuture<?> future = scheduler.schedule(
                this::handleNewToken,
                Instant.now().plusMillis(delayMillis)
        );
        futureRef.set(future);
    }
}
