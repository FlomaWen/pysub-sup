package com.floraintheo.chatrouter;

public record MessageDTO(
        String category,
        String target,
        String source,
        String timestamp,
        String payload
) {}