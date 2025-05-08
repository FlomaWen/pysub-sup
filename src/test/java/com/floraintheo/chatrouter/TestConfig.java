package com.floraintheo.chatrouter;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@TestConfiguration
@AutoConfigureBefore(GcpPubSubAutoConfiguration.class)
public class TestConfig {

    @Bean
    @Primary
    public PubSubTemplate pubSubTemplate() {
        return Mockito.mock(PubSubTemplate.class);
    }

    @Bean
    @Primary
    public CredentialsProvider googleCredentials() {
        return NoCredentialsProvider.create();
    }

    @Bean
    @Primary
    public SimpMessagingTemplate messagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
}
