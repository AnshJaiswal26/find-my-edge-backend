package com.example.find_my_edge.sheets.controller;

import com.example.find_my_edge.common.enums.ResponseState;
import com.example.find_my_edge.common.response.ApiResponse;
import com.example.find_my_edge.sheets.service.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/sheets/stream")
@RequiredArgsConstructor
public class SheetSseController {

    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping(value = "/{syncId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String syncId) {

        SseEmitter emitter = new SseEmitter(30_000L);

        sseEmitterRegistry.register(syncId, emitter);

        try {
            emitter.send(SseEmitter.event()
                                   .name("message")
                                   .data(ApiResponse.builder()
                                                    .state(ResponseState.PROGRESS)
                                                    .httpStatus(HttpStatus.SC_ACCEPTED)
                                                    .message("Sse registered")
                                                    .build())
            );
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return emitter;
    }
}
