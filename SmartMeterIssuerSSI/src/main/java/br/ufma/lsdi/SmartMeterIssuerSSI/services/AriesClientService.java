package br.ufma.lsdi.SmartMeterIssuerSSI.services;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.ErrorCode;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.InvitationException;
import br.ufma.lsdi.SmartMeterIssuerSSI.security.*;
import org.hyperledger.aries.AriesClient;
import org.hyperledger.aries.api.out_of_band.CreateInvitationFilter;
import org.hyperledger.aries.api.out_of_band.InvitationCreateRequest;
import org.hyperledger.acy_py.generated.model.InvitationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AriesClientService {
    private static final Logger log = LoggerFactory.getLogger(AriesClientService.class);

    private final AriesClient ariesClient;
    private final CurrentUserProvider currentUserProvider;

    public AriesClientService(
            AriesClient ariesAgentClient,
            CurrentUserProvider currentUserProvider
    ) {
        this.ariesClient = ariesAgentClient;
        this.currentUserProvider = currentUserProvider;
        log.info("Aries client run");
    }

    public String createInvitation() {
        log.info("Create invitation");
        String username = currentUserProvider.getUsername();

        try {
            CreateInvitationFilter invitationFilter = CreateInvitationFilter.builder()
                    .autoAccept(true)
                    .build();
            log.debug("invitationFilter {}", invitationFilter);

            InvitationCreateRequest requestInvitation = InvitationCreateRequest.builder()
                    .handshakeProtocols(List.of(
                            "https://didcomm.org/connections/1.0"
                    ))
                    .accept(List.of(
                            "didcomm/aip1",
                            "didcomm/aip2;env=rfc19"
                    ))
                    .alias(username)
                    .build();
            log.debug("RequestInvitation: {}", requestInvitation);

            InvitationRecord invitationRecord = ariesClient
                    .outOfBandCreateInvitation(requestInvitation, invitationFilter)
                    .orElseThrow(() -> {
                        log.error("Aries client returned empty response when creating invitation for user: {}", username);
                        return new InvitationException(ErrorCode.AGENT_RESPONSE_ERROR.getMessage());
                    });

            log.debug("Invitation Record: {}", invitationRecord);

            return invitationRecord.getInvitationUrl();
        } catch (IOException e) {
            log.error("I/O error while creating invitation for user {}", username, e);
            throw new InvitationException(ErrorCode.AGENT_COMMUNICATION_ERROR.getMessage(), e);
        }
    }
}