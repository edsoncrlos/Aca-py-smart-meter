package br.ufma.lsdi.SmartMeterVerifierSSI.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionHourDTO;

import java.util.*;

@Getter
public class ResourceResponse {
    private List<Resource> resources;

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    public Map<String, ConsumptionHourDTO>  getConsumptionsByHour(Map<String, ConsumptionHourDTO> consumptionHours) {

        if (resources.isEmpty()) {
            return consumptionHours;
        }

        for (Resource resource: resources) {
            List<EnergyConsumption> energyConsumptions = resource.getCapabilities().getEnergyConsumption();
            String lastHour = energyConsumptions.get(0).getHour();
            Double sumEnergyConsumption = 0.0;

            for (EnergyConsumption energyConsumption: energyConsumptions) {

                if (!energyConsumption.getHour().equals(lastHour)) {
                    ConsumptionHourDTO consumptionHourDTO = new ConsumptionHourDTO(lastHour, sumEnergyConsumption);

                    consumptionHours.merge(
                            lastHour,
                            consumptionHourDTO,
                            (o, n) -> new ConsumptionHourDTO(n.getHour(), n.getConsumption()+o.getConsumption())
                    );

                    lastHour = energyConsumption.getHour();
                    sumEnergyConsumption = 0.0;
                }
                sumEnergyConsumption += energyConsumption.getValue();
            }

            ConsumptionHourDTO consumptionHourDTO = new ConsumptionHourDTO(lastHour.toString(), sumEnergyConsumption);
            consumptionHours.merge(
                    lastHour,
                    consumptionHourDTO,
                    (o, n) -> new ConsumptionHourDTO(n.getHour(), n.getConsumption()+o.getConsumption())
            );
        }

        return consumptionHours;
    }
}

@Getter
class Resource {
    private String uuid;
    private Capabilities capabilities;
}

@Getter
class Capabilities {
    @JsonProperty("energy_consumption")
    private List<EnergyConsumption> energyConsumption;
}

@Getter
class EnergyConsumption {
    private double value;
    private String date;

    public String getHour() {
        return date.substring(11, 13);
    }
}
