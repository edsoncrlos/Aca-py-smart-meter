package br.ufma.lsdi.SmartMeterVerifierSSI.dtos;

public record TokenRequestDTO(
        String identifier,
        String signature
) {
}
