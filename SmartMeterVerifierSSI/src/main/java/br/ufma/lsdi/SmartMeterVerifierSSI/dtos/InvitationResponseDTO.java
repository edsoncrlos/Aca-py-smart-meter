package br.ufma.lsdi.SmartMeterVerifierSSI.dtos;

import java.util.UUID;

public record InvitationResponseDTO(
        UUID invitationId,
        String invitationUrl
) {
}
