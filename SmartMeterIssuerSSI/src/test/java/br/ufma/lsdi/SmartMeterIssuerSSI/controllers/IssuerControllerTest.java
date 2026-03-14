package br.ufma.lsdi.SmartMeterIssuerSSI.controllers;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterIssuerSSI.exceptions.InvitationException;
import br.ufma.lsdi.SmartMeterIssuerSSI.services.AriesClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

@WebMvcTest(IssuerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IssuerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AriesClientService ariesClientService;

    public static final String INVITATION_URL = "http://10.0.0.200:30000?oob=eyJAdHlwZSI6ICJodHRwczovL2RpZGNvbW0ub3JnL291dC1vZi1iYW5kLzEuMS9pbnZpdGF0aW9uIiwgIkBpZCI6ICIxNDAzMTcyZS0zY2UyLTRkMjUtOWZmOS0yMzhkZmUyN2E3NjkiLCAibGFiZWwiOiAiSW52aXRhdGlvbiB0byBCYXJyeSIsICJoYW5kc2hha2VfcHJvdG9jb2xzIjogWyJodHRwczovL2RpZGNvbW0ub3JnL2RpZGV4Y2hhbmdlLzEuMCJdLCAiYWNjZXB0IjogWyJkaWRjb21tL2FpcDEiLCAiZGlkY29tbS9haXAyO2Vudj1yZmMxOSJdLCAic2VydmljZXMiOiBbeyJpZCI6ICIjaW5saW5lIiwgInR5cGUiOiAiZGlkLWNvbW11bmljYXRpb24iLCAicmVjaXBpZW50S2V5cyI6IFsiZGlkOmtleTp6Nk1rbmFkelFkSkNVdVc5S28zYmVlVTZSRHhTaGFCaFZGRG5od2hwSlh0RXdnbXgjejZNa25hZHpRZEpDVXVXOUtvM2JlZVU2UkR4U2hhQmhWRkRuaHdocEpYdEV3Z214Il0sICJzZXJ2aWNlRW5kcG9pbnQiOiAiaHR0cDovLzEwLjAuMC4yMDA6MzAwMDAifV0sICJnb2FsX2NvZGUiOiAiaXNzdWUtdmMiLCAiZ29hbCI6ICJUbyBpc3N1ZSBhIEZhYmVyIENvbGxlZ2UgR3JhZHVhdGUgY3JlZGVudGlhbCJ9";
    public static final String INVITATION_ERROR_MESSAGE = "invitation error";

    @Test
    public void createInvitation_Valid_ReturnInvitationURL() throws Exception {
        when(ariesClientService.createInvitation()).thenReturn(INVITATION_URL);

        mockMvc.perform(get(ApiPaths.CREATE_INVITATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invitationUrl").value(INVITATION_URL));
    }

    @Test
    public void createInvitation_InvitationException_ReturnBadGateway() throws Exception {
        when(ariesClientService.createInvitation())
                .thenThrow(new InvitationException(INVITATION_ERROR_MESSAGE));

        mockMvc.perform(get(ApiPaths.CREATE_INVITATION))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$").value(INVITATION_ERROR_MESSAGE));
    }
}
