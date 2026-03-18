package br.ufma.lsdi.SmartMeterVerifierSSI.dtos;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ConsumptionRangeQueryDTO(
        @Schema(example = "2026-03-01")
        LocalDate day,
        @Schema(example = "-23.559616")
        Double lat,
        @Schema(example = "-46.731386")
        Double lon,
        @Schema(description = "search radius size from the point", example = "3000")
        Double radius
) {
}
