package br.ufma.lsdi.SmartMeterVerifierSSI.controllers;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionRangeQueryDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.security.Role;
import br.ufma.lsdi.SmartMeterVerifierSSI.services.SmartMeter;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
public class SmartMeterController {
    private final SmartMeter smartMeterService;

    public SmartMeterController(SmartMeter smartMeterService) {
        this.smartMeterService = smartMeterService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(ApiPaths.CONSUMPTION_BY_HOUR)
    public ResponseEntity<ConsumptionResponseDTO> getConsumptionByHourRange(
            @ParameterObject ConsumptionRangeQueryDTO consumptionRangeQueryDTO
    ) {
        ConsumptionResponseDTO consumptionResponse = smartMeterService.getResourcesConsumptionByHour(
                consumptionRangeQueryDTO.day(),
                consumptionRangeQueryDTO.lat(),
                consumptionRangeQueryDTO.lon(),
                consumptionRangeQueryDTO.radius()
        );
        return ResponseEntity.ok(consumptionResponse);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation url"),
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(ApiPaths.CONSUMPTION_BY_HOUR_ONE)
    public ResponseEntity<ConsumptionResponseDTO> getConsumptionByHour(
            @Parameter(example = "772f0fa5-87bf-4806-b365-d093e2edb6e4")
            @PathVariable("uuid") String uuid,
            @Parameter(example = "2026-03-01")
            @RequestParam LocalDate day
    ) {
        ConsumptionResponseDTO consumption = this.smartMeterService.getResourceConsumptionByHour(uuid, day);
        return ResponseEntity.ok(consumption);
    }
}
