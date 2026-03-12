package br.ufma.lsdi.SmartMeterIssuerSSI.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record InvitationUrlDTO(
        @Schema(
                description = "Invitation url",
                example = "https://example.com/invite?oob=eyJ0eXAiOiJKV1QiLCJhbGciOiJFZERTQSJ9"
        )
        String invitationUrl
) {
}
