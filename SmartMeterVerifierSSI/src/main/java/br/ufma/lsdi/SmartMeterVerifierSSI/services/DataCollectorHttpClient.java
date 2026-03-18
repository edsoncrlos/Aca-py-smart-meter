package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterVerifierSSI.models.ResourceResponse;
import br.ufma.lsdi.SmartMeterVerifierSSI.utils.InterscityCollectorQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DataCollectorHttpClient implements DataCollector {

    private final RestClient restClient;

    private final AuthenticationService authenticationService;

    DataCollectorHttpClient(
            AuthenticationFactory authenticationFactory,
            @Value("${secure.data.collector.url}") String secureDataCollectorUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(secureDataCollectorUrl)
                .build();

        this.authenticationService = authenticationFactory.create(restClient);
    }

    @Override
    public String getResourcesHistoryData(Integer index, InterscityCollectorQuery query) {

        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiPaths.GET_HISTORY_DATA_ALL_RESOURCES)
                        .queryParam("start", index)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, authenticationService.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .retrieve()
                .body(String.class);
    }

    @Override
    public String getResourceHistoryData(String uuid, Integer index, InterscityCollectorQuery query) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiPaths.GET_HISTORY_DATA_ONE_RESOURCE)
                        .queryParam("start", index)
                        .build(uuid)
                )
                .header(HttpHeaders.AUTHORIZATION, authenticationService.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .retrieve()
                .body(String.class);
    }
}
