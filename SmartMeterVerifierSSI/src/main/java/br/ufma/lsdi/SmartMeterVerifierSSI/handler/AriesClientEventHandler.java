package br.ufma.lsdi.SmartMeterVerifierSSI.handler;

import br.ufma.lsdi.SmartMeterVerifierSSI.services.AriesClientService;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.api.present_proof.PresentationExchangeRecord;
import org.hyperledger.aries.webhook.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class AriesClientEventHandler extends EventHandler {

    private final AriesClientService ariesClientService;

    public AriesClientEventHandler(AriesClientService ariesClientService) {
        this.ariesClientService = ariesClientService;
    }

    @Override
    public void handleConnection(ConnectionRecord connection) {
        if (connection.stateIsActive()) {
            ariesClientService.proofOfRequest(connection);
        }
    }

    @Override
    public void handleProof(PresentationExchangeRecord proof) {
        if (proof.stateIsPresentationReceived()) {
            ariesClientService.verifyPresentProof(proof);
        }
    }

}
