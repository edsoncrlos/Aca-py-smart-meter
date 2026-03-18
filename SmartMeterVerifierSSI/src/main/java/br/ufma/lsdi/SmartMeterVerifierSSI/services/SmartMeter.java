package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.ConsumptionResponseDTO;

import java.time.LocalDate;

public interface SmartMeter {
    ConsumptionResponseDTO getResourcesConsumptionByHour(LocalDate date, Double lat, Double lon, Double radius);
    ConsumptionResponseDTO getResourceConsumptionByHour(String uuid, LocalDate date);
}
