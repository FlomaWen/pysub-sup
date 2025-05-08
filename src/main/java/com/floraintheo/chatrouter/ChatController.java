package com.floraintheo.chatrouter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatController(PubSubTemplate pubSubTemplate,
                          ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/common")
    public MessageDTO sendMessage(@Payload MessageDTO chatMessage) throws JsonProcessingException {
        // Ajout du timestamp si nécessaire
        if (chatMessage.timestamp() == null || chatMessage.timestamp().isEmpty()) {
            String ts = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            chatMessage = new MessageDTO(
                    chatMessage.category(),
                    chatMessage.target(),
                    chatMessage.source(),
                    ts,
                    chatMessage.payload()
            );
        }

        // Sérialisation en JSON
        String json = objectMapper.writeValueAsString(chatMessage);

        // Publication du JSON (String) sur le topic Pub/Sub
        this.pubSubTemplate.publish("floriantheo-common-topic", json);

        // On renvoie l’objet pour l’affichage WebSocket
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/common")
    public MessageDTO addUser(@Payload MessageDTO chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.source());

        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new MessageDTO(
                "SYSTEM",
                "COMMON",
                "SYSTEM",
                ts,
                chatMessage.source() + " joined the chat!"
        );
    }
}
