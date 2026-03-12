package br.ufma.lsdi.SmartMeterIssuerSSI.controllers;

import br.ufma.lsdi.SmartMeterIssuerSSI.common.ApiPaths;
import br.ufma.lsdi.SmartMeterIssuerSSI.handler.AriesClientHandler;
import org.hyperledger.aries.webhook.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final EventHandler ariesClientHandler;

    public WebhookController(AriesClientHandler arieClientHandler) {
        this.ariesClientHandler = arieClientHandler;
    }

    @PostMapping(ApiPaths.WEBHOOK)
    public void handleEvents(@PathVariable String topic, @RequestBody String payload) {
        log.debug("Topic: {} | payload: {}", topic, payload);

        ariesClientHandler.handleEvent(topic, payload);
    }
}
