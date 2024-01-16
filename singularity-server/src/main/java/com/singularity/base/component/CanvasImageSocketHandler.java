package com.singularity.base.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.singularity.base.utils.JsonUtils;
import com.singularity.prediction.dto.NumImagePredictionResDto;
import com.singularity.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class CanvasImageSocketHandler extends TextWebSocketHandler {

    private final PredictionService predictionService;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.debug("## afterConnectionEstablished: {} - {}", session, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage recvMsg) throws Exception {
        log.debug("## handleTextMessage: {} - {}", session, recvMsg);
        Map<String, Object> messageMap = JsonUtils.parse(recvMsg.getPayload(), new TypeReference<>() {});

        log.debug("## handleTextMessage: {}", messageMap);
        TextMessage sendMsg = recvMsg;
        if (messageMap.containsKey("canvasData")) {
            String images = (String) messageMap.get("canvasData");
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(images.split(",")[1]));
            NumImagePredictionResDto predict = predictionService.predict(bais);
            sendMsg = new TextMessage(JsonUtils.stringify(predict));
        }

        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && session.getId().equals(webSocketSession.getId())) {
                webSocketSession.sendMessage(sendMsg);
            }
        }
    }

}
