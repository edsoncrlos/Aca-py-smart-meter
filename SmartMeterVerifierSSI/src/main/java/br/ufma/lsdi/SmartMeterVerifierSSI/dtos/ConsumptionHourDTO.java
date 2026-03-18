package br.ufma.lsdi.SmartMeterVerifierSSI.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Energy consumption data for a specific hour")
@AllArgsConstructor
@Getter
public class ConsumptionHourDTO {
    @Schema(
            description = "Hour of the day in HH:mm format",
            example = "00:00"
    )
    private String hour;

    @Setter
    @Schema(
            description = "Energy consumed during this hour in kWh",
            example = "0.098"
    )
    private Double consumption;

    @JsonProperty("consumption")
    public String getFormattedConsumption() {
        return String.format("%.3f", consumption);
    }

    @JsonProperty("hour")
    public String getFormattedHour() {
        return hour + ":00";
    }
}

