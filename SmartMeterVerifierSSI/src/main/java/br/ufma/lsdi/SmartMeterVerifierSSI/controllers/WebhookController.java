package br.ufma.lsdi.SmartMeterVerifierSSI.controllers;

import br.ufma.lsdi.SmartMeterVerifierSSI.common.ApiPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.ufma.lsdi.SmartMeterVerifierSSI.handler.AriesClientEventHandler;
import org.hyperledger.aries.webhook.EventHandler;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final EventHandler ariesClientEventHandler;

    public WebhookController(AriesClientEventHandler ariesClientEventHandler) {
        this.ariesClientEventHandler = ariesClientEventHandler;
    }

    @PostMapping(ApiPaths.WEBHOOK)
    public void handleEvents(@PathVariable String topic, @RequestBody String payload) {
        log.debug("Topic: {} | payload: {}", topic, payload);

        ariesClientEventHandler.handleEvent(topic, payload);
    }
}