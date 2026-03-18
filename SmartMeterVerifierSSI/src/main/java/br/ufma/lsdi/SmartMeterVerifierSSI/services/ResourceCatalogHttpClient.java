package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceCatalogHttpClient implements ResourceCatalog {

    private final RestClient restClient;

    private final AuthenticationService authenticationService;

    private ObjectMapper mapper;

    ResourceCatalogHttpClient (
            ObjectMapper mapper,
            AuthenticationFactory authenticationFactory,
            @Value("${secure.resource.catalog.url}") String secureResourceCatalogUrl
    ) {
        this.mapper = mapper;

        this.restClient = RestClient.builder()
                .baseUrl(secureResourceCatalogUrl)
                .build();

        this.authenticationService = authenticationFactory.create(restClient);
    }

    @Override
    public List<String> getResourcesByLocation(Double lat, Double lon, Double radius) {
        String jsonResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiPaths.GET_RESOURCES_SEARCH)
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("radius", radius)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, authenticationService.getToken())
                .retrieve()
                .body(String.class);

            JsonNode root = mapper.readTree(jsonResponse);

            List<String> uuids = new ArrayList<>();
            for (JsonNode resource : root.path("resources")) {
                uuids.add(resource.path("uuid").stringValue());
            }

            return uuids;
    }
}
