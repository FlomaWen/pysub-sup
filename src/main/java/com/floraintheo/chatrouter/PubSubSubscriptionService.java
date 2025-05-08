package com.floraintheo.chatrouter;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@Configuration
public class PubSubSubscriptionService {

    private static final Logger logger = Logger.getLogger(PubSubSubscriptionService.class.getName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Channels d’entrée
    @Bean
    public MessageChannel commonMessageChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel userMessageChannel() {
        return new DirectChannel();
    }

    // Adapter pour "common"
    @Bean
    public PubSubInboundChannelAdapter commonMessageAdapter(
            PubSubTemplate pubSubTemplate,
            MessageChannel commonMessageChannel) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, "sub-floriantheo-common");
        adapter.setOutputChannel(commonMessageChannel);
        adapter.setPayloadType(MessageDTO.class);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }

    // Adapter pour "user"
    @Bean
    public PubSubInboundChannelAdapter userMessageAdapter(
            PubSubTemplate pubSubTemplate,
            MessageChannel userMessageChannel) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, "sub-floriantheo-user");
        adapter.setOutputChannel(userMessageChannel);
        adapter.setPayloadType(MessageDTO.class);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }

    // Handler des messages "common"
    @ServiceActivator(inputChannel = "commonMessageChannel")
    public void commonMessageReceiver(
            @Payload MessageDTO dto,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage original) {

        logger.info("Common message received: " + dto);
        messagingTemplate.convertAndSend("/topic/common", dto);

        if (original != null) {
            original.ack();
        }
    }

    // Handler des messages "user"
    @ServiceActivator(inputChannel = "userMessageChannel")
    public void userMessageReceiver(
            @Payload MessageDTO dto,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage original) {

        logger.info("User message received: " + dto);
        messagingTemplate.convertAndSend("/topic/user/" + dto.target(), dto);

        if (original != null) {
            original.ack();
        }
    }
}
