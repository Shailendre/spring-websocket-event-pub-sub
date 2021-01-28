package com.lazycompiler.poc.listener;

import com.lazycompiler.poc.SocketHandler;
import com.lazycompiler.poc.dto.StreamingData;

import com.google.gson.Gson;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataEventListener {

    private final SocketHandler socketHandler;

    public DataEventListener(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @EventListener
    public void handleStreamingDataEvent(StreamingData message) {
        String sessionId = message.getId();
        List<WebSocketSession> sessions = socketHandler.getSession(sessionId);
        log.info("Found {} sessions with {} ", sessions.size(), sessionId);
        sessions.forEach(webSocketSession -> {
            try {
                log.info("Sending message to all Open Session for {}", sessionId);
                Gson gson = new Gson();
                String jsonData = gson.toJson(message);
                Map<String, String> textMessage = Collections.singletonMap("name", jsonData);
                socketHandler.handleTextMessage(webSocketSession, new TextMessage(gson.toJson(textMessage)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
