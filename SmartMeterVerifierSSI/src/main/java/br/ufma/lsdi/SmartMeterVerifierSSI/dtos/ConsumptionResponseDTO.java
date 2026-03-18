package br.ufma.lsdi.SmartMeterVerifierSSI.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Schema(description = "Daily energy consumption grouped by hour")
@JsonPropertyOrder({"date", "unit", "consumption"})
@Getter
public class ConsumptionResponseDTO {
    @Schema(example = "2028-03-02")
    private final LocalDate date;
    @Schema(
            description = "Map where key is hour (00-23) and value is consumption data",
            example = """
                    [
                            { "hour": "00:00", "consumption": "0.098" },
                            { "hour": "01:00", "consumption": "0.089" },
                            { "hour": "02:00", "consumption": "0.087" },
                            { "hour": "03:00", "consumption": "0.086" },
                            { "hour": "04:00", "consumption": "0.086" },
                            { "hour": "05:00", "consumption": "0.088" },
                            { "hour": "06:00", "consumption": "0.081" },
                            { "hour": "07:00", "consumption": "0.049" },
                            { "hour": "08:00", "consumption": "0.085" },
                            { "hour": "09:00", "consumption": "0.119" },
                            { "hour": "10:00", "consumption": "0.050" },
                            { "hour": "11:00", "consumption": "0.070" },
                            { "hour": "12:00", "consumption": "0.061" },
                            { "hour": "13:00", "consumption": "0.105" },
                            { "hour": "14:00", "consumption": "0.064" },
                            { "hour": "15:00", "consumption": "0.090" },
                            { "hour": "16:00", "consumption": "0.061" },
                            { "hour": "17:00", "consumption": "0.076" },
                            { "hour": "18:00", "consumption": "0.083" },
                            { "hour": "19:00", "consumption": "0.100" },
                            { "hour": "20:00", "consumption": "0.103" },
                            { "hour": "21:00", "consumption": "0.135" },
                            { "hour": "22:00", "consumption": "0.142" },
                            { "hour": "23:00", "consumption": "0.122" }
                        ]
                    """
    )
    private final List<ConsumptionHourDTO> consumptions;

    @Schema(example = "kWh")
    private final String unit = "kWh";

    public ConsumptionResponseDTO(LocalDate date, List<ConsumptionHourDTO> consumptions) {
        this.date = date;
        this.consumptions = consumptions;
    }
}
