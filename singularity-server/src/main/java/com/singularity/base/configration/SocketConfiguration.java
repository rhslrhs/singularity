package com.singularity.base.configration;

import com.singularity.base.component.CanvasImageSocketHandler;
import com.singularity.base.component.SocketHandler;
import com.singularity.prediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SocketConfiguration implements WebSocketConfigurer {
    private final SocketHandler socketHandler;
    private final CanvasImageSocketHandler canvasImageSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/socket").setAllowedOrigins("*");
        registry.addHandler(canvasImageSocketHandler, "/ws/images").setAllowedOrigins("*");
    }
}