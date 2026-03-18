package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.Interscity;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionHourDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.exceptions.NumberResourcesException;
import br.ufma.lsdi.SmartMeterVerifierSSI.models.ResourceResponse;
import br.ufma.lsdi.SmartMeterVerifierSSI.utils.InterscityCollectorQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SmartMeterService implements SmartMeter {
    private static final Logger log = LoggerFactory.getLogger(SmartMeterService.class);

    private final DataCollector collectorClient;
    private final ResourceCatalog catalogClient;
    private final ObjectMapper mapper;

    SmartMeterService(
            DataCollectorHttpClient dataCollectorHttpClient,
            ResourceCatalogHttpClient resourceCatalogHttpClient,
            ObjectMapper mapper) {
        this.collectorClient = dataCollectorHttpClient;
        this.catalogClient = resourceCatalogHttpClient;
        this.mapper = mapper;
    }

    @Override
    public ConsumptionResponseDTO getResourcesConsumptionByHour(LocalDate date, Double lat, Double lon, Double radius) {

        String startDate = date
                .atStartOfDay()
                .format(DateTimeFormatter.ISO_DATE_TIME);

        String endDate = date
                .atTime(23, 59, 59)
                .format(DateTimeFormatter.ISO_DATE_TIME);

        // Get uuids in range
        List<String> uuids = catalogClient.getResourcesByLocation(lat, lon, radius);
        Integer resources_size = uuids.size();

        log.debug("uuids: {}", uuids);
        log.debug("number uuids: {}", resources_size);

        if (resources_size < 3) {
            throw new NumberResourcesException("must be more 3");
        }

        // Build query to DataCollector
        InterscityCollectorQuery query = InterscityCollectorQuery.builder()
                .capabilities(Interscity.CAPABILITIES)
                .uuids(uuids)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        ResourceResponse resourceResponse;
        Map<String, ConsumptionHourDTO> consumptionHours = new LinkedHashMap<String, ConsumptionHourDTO>();

        try {
            for (int i = 0; true; i+=1000) {
                if (log.isDebugEnabled()) {
                    log.debug("Page: {}", i/1000+1);
                }

                String data = collectorClient.getResourcesHistoryData(i, query);
                resourceResponse = mapper.readValue(data, ResourceResponse.class);

                consumptionHours = resourceResponse.getConsumptionsByHour(consumptionHours);

                if (resourceResponse.isEmpty()) {
                    consumptionHours.replaceAll((k, v) -> (
                            new ConsumptionHourDTO(k, v.getConsumption()/resources_size)
                    ));
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ConsumptionResponseDTO(
                date,
                new ArrayList<>(consumptionHours.values())
        );
    }

    @Override
    public ConsumptionResponseDTO getResourceConsumptionByHour(String uuid, LocalDate date) {

        String startDate = date
                .atStartOfDay()
                .format(DateTimeFormatter.ISO_DATE_TIME);

        String endDate = date
                .atTime(23, 59, 59)
                .format(DateTimeFormatter.ISO_DATE_TIME);


        // Build query to DataCollector
        InterscityCollectorQuery query = InterscityCollectorQuery.builder()
                .capabilities(Interscity.CAPABILITIES)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        log.debug("uuid {}", uuid);
        try {
            ResourceResponse resourceResponse;
            Map<String, ConsumptionHourDTO> consumptionHours = new LinkedHashMap<>();

            for (int i = 0; true; i+=1000) {
                if (log.isDebugEnabled()) {
                    log.debug("Page: {}", i/1000+1);
                }

                String data = collectorClient.getResourceHistoryData(uuid, i, query);
                resourceResponse = mapper.readValue(data, ResourceResponse.class);

                consumptionHours = resourceResponse.getConsumptionsByHour(consumptionHours);

                if (resourceResponse.isEmpty()) {
                    break;
                }

            }

            return new ConsumptionResponseDTO(
                    date,
                    new ArrayList<>(consumptionHours.values())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
