package com.lassriver.bookworm.services.ai;

import java.util.List;
import java.util.function.Consumer;

public interface AiChatClient {
    void stream(List<AiMessage> messages, String providerApiKey, Consumer<String> onChunk);
}
