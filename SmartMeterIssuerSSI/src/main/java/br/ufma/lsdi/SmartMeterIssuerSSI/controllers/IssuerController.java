package br.ufma.lsdi.SmartMeterIssuerSSI.controllers;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterIssuerSSI.dto.InvitationUrlDTO;
import br.ufma.lsdi.SmartMeterIssuerSSI.services.AriesClientService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuerController {
    private final AriesClientService ariesClientService;

    public IssuerController(AriesClientService ariesClientService) {
        this.ariesClientService = ariesClientService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation url"),
    })
    @GetMapping(ApiPaths.CREATE_INVITATION)
    public ResponseEntity<InvitationUrlDTO> createInvitation() {
        InvitationUrlDTO invitationUrlDTO = new InvitationUrlDTO(ariesClientService.createInvitation());
        return ResponseEntity.ok(invitationUrlDTO);
    }
}
