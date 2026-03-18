//package br.ufma.lsdi.SmartMeterVerifierSSI.services;
//
//import br.ufma.lsdi.SmartMeterVerifierSSI.dtos.UserDTO;
//import com.google.gson.JsonObject;
//import org.hyperledger.aries.AriesClient;
//import org.hyperledger.aries.api.connection.ConnectionRecord;
//import org.hyperledger.aries.api.present_proof.PresentationExchangeRecord;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.IOException;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AriesClientServiceTests {
//
//    @Mock
//    private AriesClient ariesClient;
//
//    @Mock
//    private JwtService jwtService;
//
//    @InjectMocks
//    private AriesClientService service;
//
//    @Nested
//    class ProofOfRequestTests {
//
//        @Test
//        void proofOfRequest_validConnection_invokesClientWithCorrectRequest() throws IOException {
//            // arrange
//            ConnectionRecord connection = mock(ConnectionRecord.class);
//            when(connection.getConnectionId()).thenReturn("conn-123");
//
//            // act
//            service.proofOfRequest(connection);
//
//            // assert
//            ArgumentCaptor<org.hyperledger.aries.api.present_proof.PresentProofRequest> captor =
//                    ArgumentCaptor.forClass(org.hyperledger.aries.api.present_proof.PresentProofRequest.class);
//
//            verify(ariesClient).presentProofSendRequest(captor.capture());
//            org.hyperledger.aries.api.present_proof.PresentProofRequest request = captor.getValue();
//            assertThat(request.getConnectionId()).isEqualTo("conn-123");
//            assertThat(request.getComment()).isEqualTo("Provando o seu papel");
//            assertThat(request.getProofRequest()).isNotNull();
//        }
//
//        @Test
//        void proofOfRequest_clientIOException_throwsRuntimeException() throws IOException {
//            ConnectionRecord connection = mock(ConnectionRecord.class);
//            when(connection.getConnectionId()).thenReturn("anything");
//            doThrow(new IOException("boom")).when(ariesClient).presentProofSendRequest(any());
//
//            assertThatThrownBy(() -> service.proofOfRequest(connection))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasCauseInstanceOf(IOException.class)
//                    .hasMessageContaining("boom");
//        }
//    }
//
//    @Nested
//    class VerifyPresentProofTests {
//
//        @Test
//        void verifyPresentProof_initiatorNotSelf_noClientInteraction() {
//            PresentationExchangeRecord proof = mock(PresentationExchangeRecord.class);
//            when(proof.initiatorIsSelf()).thenReturn(false);
//
//            service.verifyPresentProof(proof);
//
//            verifyNoInteractions(ariesClient, jwtService);
//        }
//
//        @Test
//        void verifyPresentProof_initiatorSelf_validPresentation_generatesToken() {
//            PresentationExchangeRecord proof = mock(PresentationExchangeRecord.class);
//            when(proof.initiatorIsSelf()).thenReturn(true);
//            when(proof.getPresExId()).thenReturn("pres-id");
//            when(proof.getConnectionId()).thenReturn("conn-xyz");
//
//            PresentationExchangeRecord responseRecord = mock(PresentationExchangeRecord.class);
//            when(ariesClient.presentProofRecordsVerifyPresentation("pres-id"))
//                    .thenReturn(Optional.of(responseRecord));
//
//            // build the nested JSON structure expected by the service
//            JsonObject attr = new JsonObject();
//            attr.addProperty("raw", "ROLE_USER");
//
//            JsonObject revealed = new JsonObject();
//            revealed.add("attr1_referent", attr);
//
//            JsonObject requestedProof = new JsonObject();
//            requestedProof.add("revealed_attrs", revealed);
//
//            JsonObject presentation = new JsonObject();
//            presentation.add("requested_proof", requestedProof);
//
//            when(responseRecord.getPresentation()).thenReturn(presentation);
//            when(responseRecord.getPresentationRequest()).thenReturn(new Object()); // unused
//
//            service.verifyPresentProof(proof);
//
//            ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
//            verify(jwtService).generateToken(captor.capture());
//            UserDTO dto = captor.getValue();
//            assertThat(dto.connectionId()).isEqualTo("conn-xyz");
//            assertThat(dto.role()).isEqualTo("ROLE_USER");
//        }
//
//        @Test
//        void verifyPresentProof_verifyPresentationReturnsEmpty_throwsRuntimeException() {
//            PresentationExchangeRecord proof = mock(PresentationExchangeRecord.class);
//            when(proof.initiatorIsSelf()).thenReturn(true);
//            when(proof.getPresExId()).thenReturn("pres-id");
//
//            when(ariesClient.presentProofRecordsVerifyPresentation("pres-id"))
//                    .thenReturn(Optional.empty());
//
//            assertThatThrownBy(() -> service.verifyPresentProof(proof))
//                    .isInstanceOf(RuntimeException.class)
//                    .hasMessageContaining("pres_ex_id");
//        }
//    }
//}
