package com.singularity.base.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.singularity.base.utils.JsonUtils;
import com.singularity.prediction.dto.NumberImageBase64StrPredictionReqDto;
import com.singularity.prediction.dto.NumberImagePredictionResDto;
import com.singularity.prediction.service.NumberImagePredictionService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class CanvasImageSocketHandler extends TextWebSocketHandler {

    private final NumberImagePredictionService predictionService;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.debug("## afterConnectionEstablished: {} - {}", session, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage recvMsg) throws Exception {
//        log.debug("## handleTextMessage: {} - {}", session, recvMsg);
        Map<String, Object> messageMap = JsonUtils.parse(recvMsg.getPayload(), new TypeReference<>() {});

//        log.debug("## handleTextMessage: {}", messageMap);
        TextMessage sendMsg = recvMsg;

        if (messageMap.containsKey("imageData")) {
            NumberImageBase64StrPredictionReqDto reqDto = NumberImageBase64StrPredictionReqDto.builder()
                .base64Str((String) messageMap.get("imageData"))
                .build();

            NumberImagePredictionResDto predict = predictionService.predict(reqDto);
            sendMsg = new TextMessage(JsonUtils.stringify(predict));
        }

        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && session.getId().equals(webSocketSession.getId())) {
                webSocketSession.sendMessage(sendMsg);
            }
        }
    }

}
