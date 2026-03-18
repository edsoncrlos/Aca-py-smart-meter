package br.ufma.lsdi.SmartMeterVerifierSSI.controllers;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.InvitationResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.TokenInvitationRequestDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.TokenResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.services.AriesClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class VerifierController {
    private final AriesClientService ariesClientService;

    public VerifierController(AriesClientService ariesClientService) {
        this.ariesClientService = ariesClientService;
    }

    @GetMapping(ApiPaths.CREATE_INVITATION)
    public ResponseEntity<InvitationResponseDTO> createInvitation() {
        return ResponseEntity.ok(ariesClientService.createInvitation());
    }

    @PostMapping("token")
    public ResponseEntity<TokenResponseDTO> getToken(@RequestBody TokenInvitationRequestDTO tokenInvitationRequestDTO) {
        TokenResponseDTO token = new TokenResponseDTO(
                ariesClientService.getToken(tokenInvitationRequestDTO.invitationId())
        );
        return ResponseEntity.ok(token);
    }
}
