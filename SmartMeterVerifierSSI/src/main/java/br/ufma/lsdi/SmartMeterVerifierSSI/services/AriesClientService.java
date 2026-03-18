package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import org.hyperledger.aries.api.connection.ConnectionFilter;
import br.ufma.lsdi.SmartMeterVerifierSSI.common.ErrorCode;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.InvitationResponseDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.UserDTO;
import br.ufma.lsdi.SmartMeterVerifierSSI.exceptions.InvitationException;
import com.google.gson.JsonObject;
import org.hyperledger.acy_py.generated.model.InvitationRecord;
import org.hyperledger.aries.api.connection.ConnectionFilter;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.api.out_of_band.CreateInvitationFilter;
import org.hyperledger.aries.api.out_of_band.InvitationCreateRequest;
import org.hyperledger.aries.api.present_proof.PresentProofRequest;
import org.hyperledger.aries.api.present_proof.PresentProofRequest.ProofRequest;
import org.hyperledger.aries.api.present_proof.PresentProofRequest.ProofRequest.ProofRequestedAttributes;
import org.hyperledger.aries.api.present_proof.PresentationExchangeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hyperledger.aries.AriesClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.hyperledger.aries.config.CredDefId.CRED_DEF_ID;

@Service
public class AriesClientService {
    private static final Logger log = LoggerFactory.getLogger(AriesClientService.class);

    private final ConcurrentHashMap<String, String> presentationProofs = new ConcurrentHashMap<String, String>();

    private final AriesClient ariesClient;
    private final JwtService jwtService;

    public AriesClientService(AriesClient ariesAgentClient, JwtService jwtService) {
        this.ariesClient = ariesAgentClient;
        this.jwtService = jwtService;

       /* UserDTO userDTO = new UserDTO("wfef", "ROLE_USER");
        var token = jwtService.generateToken(userDTO);

        userDTO = new UserDTO("f34", "ROLE_EMPLOYEE");
        var token2 = jwtService.generateToken(userDTO);*/



        log.info("Aries client run");
    }

    public InvitationResponseDTO createInvitation() {

        try {
            UUID invitationId = UUID.randomUUID();

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
                    .alias(invitationId.toString())
                    .build();
            log.debug("RequestInvitation: {}", requestInvitation);

            InvitationRecord invitationRecord = ariesClient
                    .outOfBandCreateInvitation(requestInvitation, invitationFilter)
                    .orElseThrow(() -> {
                        log.error("Aries client returned empty response when creating invitation");
                        return new InvitationException(ErrorCode.AGENT_RESPONSE_ERROR.getMessage());
                    });

            log.debug("Invitation Record: {}", invitationRecord);

            return new InvitationResponseDTO(invitationId, invitationRecord.getInvitationUrl());
        } catch (IOException e) {
            log.error("I/O error while creating invitation", e);
            throw new InvitationException(ErrorCode.AGENT_COMMUNICATION_ERROR.getMessage(), e);
        }
    }

    public void proofOfRequest(ConnectionRecord connection) {
        var connectionId = connection.getConnectionId();

        JsonObject restriction = new JsonObject();
        restriction.addProperty(CRED_DEF_ID, "HcN1iDVHMd7ULbYmNMyZCh:3:CL:3101353:default");

        ProofRequestedAttributes proofRequestedAttributes = ProofRequestedAttributes.builder()
                .name("role")
                .restriction(restriction)
                .build();
        log.debug("Proof Requested Attributes: {}", proofRequestedAttributes);

        ProofRequest proofRequest = ProofRequest.builder()
                .name("Qual o seu no papel")
                .version("1.0")
                .requestedAttribute("attr1_referent", proofRequestedAttributes)
                .build();

        log.debug("connectionId: {}", connectionId);
        PresentProofRequest presentProofRequest = PresentProofRequest.builder()
                .connectionId(connectionId)
                .proofRequest(proofRequest)
                .comment("Provando o seu papel")
                .build();
        log.debug("Present Proof Request: {}", presentProofRequest);

        try {
            ariesClient.presentProofSendRequest(presentProofRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyPresentProof(PresentationExchangeRecord proof) {
        if (proof.initiatorIsSelf()) {
            try {
                var presentationExchangeRecord = ariesClient
                        .presentProofRecordsVerifyPresentation(proof.getPresExId())
                        .orElseThrow(() -> new RuntimeException("pres_ex_id"));
                log.debug("presentation Exchange Record: {}", presentationExchangeRecord);

                var proofRequest = presentationExchangeRecord.getPresentationRequest();
                log.debug("Proof Request: {}", proofRequest);

                String role = getRole(presentationExchangeRecord);
                presentationProofs.put(proof.getConnectionId(), role);

                log.debug("Role: {}", role);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getRole(PresentationExchangeRecord presentationExchangeRecord) {
        var presentation = presentationExchangeRecord.getPresentation();

        JsonObject requestedProof = presentation
                .getAsJsonObject("requested_proof");

        JsonObject revealedAttrs = requestedProof
                .getAsJsonObject("revealed_attrs");

        JsonObject attr = revealedAttrs
                .getAsJsonObject("attr1_referent");

        return attr
                .get("raw")
                .getAsString();
    }

    public String getToken(String invitationId) {

         ConnectionFilter connectionFilter = ConnectionFilter.builder()
                .alias(invitationId)
                .build();

        log.debug("ConnectionFilter: {}", connectionFilter);
        try {
            String connectionId = ariesClient
                    .connections(connectionFilter)
                    .orElseThrow(() -> {
                        log.warn("");
                        return new RuntimeException("");
                    }).get(0)
                    .getConnectionId();
            log.debug("ConnectionId: {}", connectionId);

            String role = presentationProofs.get(connectionId);
            UserDTO userDTO = new UserDTO(connectionId, role);
            presentationProofs.remove(connectionId);

            return jwtService.generateToken(userDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
