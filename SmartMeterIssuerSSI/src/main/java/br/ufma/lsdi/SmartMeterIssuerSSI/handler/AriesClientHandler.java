package br.ufma.lsdi.SmartMeterIssuerSSI.handler;

import br.ufma.lsdi.SmartMeterIssuerSSI.services.AriesClientService;
import org.hyperledger.aries.api.connection.ConnectionRecord;
import org.hyperledger.aries.webhook.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AriesClientHandler extends EventHandler {
    private static final Logger log = LoggerFactory.getLogger(AriesClientHandler.class);

    private final AriesClientService ariesClientService;

    public AriesClientHandler(AriesClientService ariesClientService) {
        this.ariesClientService = ariesClientService;
    }

    @Override
    public void handleConnection(ConnectionRecord connection) {
        log.debug("connection: {}", connection);
        if (connection.stateIsActive()) {
            ariesClientService.issuerSmartMeterRole(connection);
        }
    }
}
