package com.floraintheo.chatrouter;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publish")
public class PubSubController {
    private final PubSubTemplate pubSubTemplate;
    public PubSubController(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostMapping
    public ResponseEntity<String> publish(@RequestBody MessageDTO msg) {
        String topic = "floriantheo-" +
                (msg.target().equals("COMMON") ? "common-topic" : "user-topic");
        pubSubTemplate.publish(topic, msg);
        return ResponseEntity.ok("Routé vers " + topic);
    }
}


