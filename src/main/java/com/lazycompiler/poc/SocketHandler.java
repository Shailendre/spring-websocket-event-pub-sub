package com.lazycompiler.poc;

import com.lazycompiler.poc.dto.StreamingData;

import com.google.gson.Gson;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {

	private static final Map<String, List<WebSocketSession>> SESSIONS = new ConcurrentHashMap<>();

	/**
	 *
	 * @param session
	 * @param message
	 * @throws InterruptedException
	 * @throws IOException
	 *
	 *
	 * {
	 *     "type":"init_from_client" / "message_from_server"
	 *     "message": "text_message_from_server"
	 *     "id": <>docId</>
	 * }
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		log.info("Message Payload received! : {}",message.getPayload());
		Gson gson = new Gson();
		Map value = gson.fromJson(message.getPayload(), Map.class);

		String json = (String) value.get("name");
		log.info("Json message from client {}", json);


		StreamingData data = gson.fromJson(json, StreamingData.class);

		String type = data.getType();
		log.info("Message type: {}", type);

		switch (type) {
		case "init_from_client":
			String id = data.getId();
			List<WebSocketSession> idSessions = SESSIONS.getOrDefault(id, new ArrayList<>());
			idSessions.add(session);
			SESSIONS.put(id, idSessions);
			log.info("Sessions cached {}", SESSIONS);
			break;
		case "message_from_server":
			session.sendMessage(message);
			break;
		default:
			log.error("Default message type received {} !!", type);

		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// nothing to be done for now
	}

	public List<WebSocketSession> getSession(String docId){
		log.info("Current sessions {}", SESSIONS);
		if (!SESSIONS.containsKey(docId)) {
			log.error("No sessions found for document id {}", docId);
			return Collections.emptyList();
		}
		return SESSIONS.get(docId);
	}

	@PostConstruct
	public void destroySessions() {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(() -> {
			SESSIONS.forEach((s, webSocketSessions) -> {
				webSocketSessions.forEach(webSocketSession -> {
					try {
						log.info("Closing Session {}", webSocketSession);
						webSocketSession.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		}, 10, 10, TimeUnit.MINUTES);
	}

}