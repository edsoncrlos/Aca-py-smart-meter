package br.ufma.lsdi.SmartMeterIssuerSSI.services;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.ErrorCode;
import br.ufma.lsdi.SmartMeterIssuerSSI.security.*;
import org.hyperledger.aries.AriesClient;
import org.hyperledger.acy_py.generated.model.InvitationRecord;
import org.hyperledger.aries.api.out_of_band.InvitationCreateRequest;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.api.credentials.CredentialAttributes;
import org.hyperledger.aries.api.credentials.CredentialPreview;
import org.hyperledger.aries.api.issue_credential_v1.V1CredentialExchange;
import org.hyperledger.aries.api.issue_credential_v1.V1CredentialOfferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.InvitationException;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.IssuerException;

@ExtendWith(MockitoExtension.class)
class AriesClientServiceTest {
    private static final String TEST_USERNAME = "alice";
    private static final String INVITATION_URL = "http://example.com/invitation";
    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    private static final String CONNECTION_ID = "fb3cb5b7-447b-4e2e-a7a0-ad1a9d03c0d7";

    @Mock
    private UserRoleAttributeResolver roleResolver;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private AriesClient ariesClient;

    @InjectMocks
    private AriesClientService ariesClientService;

    @Nested
    class CreateInvitationTests {
        @BeforeEach
        void setupCreateInvitationTests() {
            when(currentUserProvider.getUsername()).thenReturn(TEST_USERNAME);
        }

        @Test
        void createInvitation_ValidData_returnsInvitationUrl() throws IOException {
            InvitationRecord invitationRecord = mock(InvitationRecord.class);
            when(invitationRecord.getInvitationUrl()).thenReturn(INVITATION_URL);
            when(ariesClient.outOfBandCreateInvitation(any(), any())).thenReturn(Optional.of(invitationRecord));

            String url = ariesClientService.createInvitation();

            ArgumentCaptor<InvitationCreateRequest> captor =
                    ArgumentCaptor.forClass(InvitationCreateRequest.class);

            verify(ariesClient).outOfBandCreateInvitation(
                    captor.capture(),
                    any()
            );

            InvitationCreateRequest sentRequest = captor.getValue();

            assertThat(TEST_USERNAME).isEqualTo(sentRequest.getAlias());
            assertThat(url).isEqualTo(INVITATION_URL);
        }

        @Test
        void createInvitation_EmptyOptional_ThrowInvitationException() throws IOException {
            when(ariesClient.outOfBandCreateInvitation(any(), any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ariesClientService.createInvitation())
                    .isInstanceOf(InvitationException.class)
                    .hasMessage(ErrorCode.AGENT_RESPONSE_ERROR.getMessage());
        }

        @Test
        void createInvitation_IOException_ThrowInvitationException() throws IOException {
            when(ariesClient.outOfBandCreateInvitation(any(), any())).thenThrow(new IOException("Aries client IO exception"));

            assertThatThrownBy(() -> ariesClientService.createInvitation())
                    .isInstanceOf(InvitationException.class)
                    .hasMessage(ErrorCode.AGENT_COMMUNICATION_ERROR.getMessage())
                    .hasCauseInstanceOf(IOException.class);
        }
    }

    @Nested
    class IssuerSmartMeterRoleTests {
        private ConnectionRecord connectionRecord;

        @BeforeEach
        void setupCreateInvitationTests() {
            connectionRecord = mock(ConnectionRecord.class);
            when(connectionRecord.getConnectionId()).thenReturn(CONNECTION_ID);
        }

        @Test
        void issuerSmartMeterRole_ValidData() throws IOException {
            V1CredentialExchange exchange = mock(V1CredentialExchange.class);

            when(connectionRecord.getAlias()).thenReturn(TEST_USERNAME);
            when(roleResolver.resolve(TEST_USERNAME)).thenReturn(ROLE_EMPLOYEE);
            when(ariesClient.issueCredentialSendOffer(any())).thenReturn(Optional.of(exchange));

            ariesClientService.issuerSmartMeterRole(connectionRecord);

            ArgumentCaptor<V1CredentialOfferRequest> captor =
                    ArgumentCaptor.forClass(V1CredentialOfferRequest.class);

            verify(ariesClient).issueCredentialSendOffer(captor.capture());

            V1CredentialOfferRequest sentRequest = captor.getValue();
            CredentialPreview preview = sentRequest.getCredentialPreview();
            CredentialAttributes attr = preview.getAttributes().get(0);

            assertThat(sentRequest.getConnectionId()).isEqualTo(CONNECTION_ID);
            assertThat(attr.getValue()).isEqualTo(ROLE_EMPLOYEE);
        }

        @Test
        void issuerSmartMeterRole_EmptyOptional_IssuerException() throws IOException {
            when(ariesClient.issueCredentialSendOffer(any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> ariesClientService.issuerSmartMeterRole(connectionRecord))
                    .isInstanceOf(IssuerException.class)
                    .hasMessage(ErrorCode.AGENT_RESPONSE_ERROR.getMessage());
        }

        @Test
        void issuerSmartMeterRole_AccessDeniedException_IssuerException() throws Exception {
            when(roleResolver.resolve(any())).thenThrow(new AccessDeniedException("no permission"));

            assertThatThrownBy(() -> ariesClientService.issuerSmartMeterRole(connectionRecord))
                    .isInstanceOf(IssuerException.class)
                    .hasMessage(ErrorCode.USER_NO_PERMISSION.getMessage())
                    .hasCauseInstanceOf(AccessDeniedException.class);
        }

        @Test
        void issuerSmartMeterRole_IOException_IssuerException() throws IOException {
            when(ariesClient.issueCredentialSendOffer(any())).thenThrow(new IOException("Aries client IO exception"));

            assertThatThrownBy(() -> ariesClientService.issuerSmartMeterRole(connectionRecord))
                    .isInstanceOf(IssuerException.class)
                    .hasMessage(ErrorCode.AGENT_COMMUNICATION_ERROR.getMessage())
                    .hasCauseInstanceOf(IOException.class);
        }
    }
}
