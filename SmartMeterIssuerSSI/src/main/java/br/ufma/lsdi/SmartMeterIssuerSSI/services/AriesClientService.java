package br.ufma.lsdi.SmartMeterIssuerSSI.services;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.Credentials;
import br.ufma.lsdi.SmartMeterIssuerSSI.common.ErrorCode;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.InvitationException;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.IssuerException;
import br.ufma.lsdi.SmartMeterIssuerSSI.security.*;
import org.hyperledger.aries.api.credentials.CredentialAttributes;
import org.hyperledger.aries.api.credentials.CredentialPreview;
import org.hyperledger.aries.AriesClient;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.api.issue_credential_v1.V1CredentialExchange;
import org.hyperledger.aries.api.issue_credential_v1.V1CredentialOfferRequest;
import org.hyperledger.aries.api.out_of_band.CreateInvitationFilter;
import org.hyperledger.aries.api.out_of_band.InvitationCreateRequest;
import org.hyperledger.acy_py.generated.model.InvitationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class AriesClientService {
    private static final Logger log = LoggerFactory.getLogger(AriesClientService.class);

    private final AriesClient ariesClient;
    private final CurrentUserProvider currentUserProvider;
    private final UserRoleAttributeResolver roleResolver;

    public AriesClientService(
            AriesClient ariesAgentClient,
            CurrentUserProvider currentUserProvider,
            UserRoleAttributeResolver userRoleAttributeResolver
    ) {
        this.ariesClient = ariesAgentClient;
        this.currentUserProvider = currentUserProvider;
        this.roleResolver = userRoleAttributeResolver;
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

    public void issuerSmartMeterRole(ConnectionRecord connectionRecord) {
        log.info("Issuer smart meter role");
        String username = connectionRecord.getAlias();
        String connectionId = connectionRecord.getConnectionId();

        try {
            String role = roleResolver.resolve(username);

            CredentialAttributes roleAttribute = CredentialAttributes.builder()
                    .name("role")
                    .value(role)
                    .build();

            CredentialPreview credentialPreview = new CredentialPreview(
                    "issue-credential/1.0/credential-preview",
                    List.of(roleAttribute)
            );

            V1CredentialOfferRequest credentialOfferRequest = V1CredentialOfferRequest.builder()
                    .connectionId(connectionId)
                    .credentialDefinitionId("HcN1iDVHMd7ULbYmNMyZCh:3:CL:3101353:default")
                    .comment(Credentials.SMART_METER_ROLE_DESCRIPTION)
                    .credentialPreview(credentialPreview)
                    .autoIssue(true)
                    .build();
            log.debug("credentialOfferRequest {}", credentialOfferRequest);

            V1CredentialExchange credentialExchange = ariesClient
                    .issueCredentialSendOffer(credentialOfferRequest)
                    .orElseThrow(() -> {
                        log.error("Aries client returned empty response when try issuer for user: {}", username);
                        return new IssuerException(ErrorCode.AGENT_RESPONSE_ERROR.getMessage());
                    });

            log.debug("credentialExchange {}", credentialExchange);
        } catch (AccessDeniedException e) {
            log.error("roleAttributes: User: {} don't have permission", username);
            throw new IssuerException(ErrorCode.USER_NO_PERMISSION.getMessage(), e);
        } catch (IOException e) {
            log.error("Issuer Smart Role error for User: {}; connectionId: {}", username, connectionId, e);
            throw new IssuerException(ErrorCode.AGENT_COMMUNICATION_ERROR.getMessage(), e);
        }
    }
}