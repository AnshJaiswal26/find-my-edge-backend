package com.example.find_my_edge.integrations.sheets.service;

import com.example.find_my_edge.common.response.ApiResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {

    private final Map<String, SseEmitter> emitters =
            new ConcurrentHashMap<>();

    public void register(String syncId, SseEmitter emitter) {
        emitters.put(syncId, emitter);

        emitter.onCompletion(() -> emitters.remove(syncId));
        emitter.onTimeout(() -> emitters.remove(syncId));
        emitter.onError(e -> emitters.remove(syncId));
    }

    public void send(String syncId, ApiResponse<Object> response) {
        SseEmitter emitter = emitters.get(syncId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                                   .name("message")
                                   .data(response));
        } catch (IOException e) {
            emitters.remove(syncId);
        }
    }

    public void complete(String syncId) {
        SseEmitter emitter = emitters.remove(syncId);
        if (emitter != null) {
            emitter.complete();
        }
    }
}
