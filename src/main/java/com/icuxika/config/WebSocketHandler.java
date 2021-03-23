package com.icuxika.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class WebSocketHandler extends AbstractWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ApiHeader apiHeader = (ApiHeader) session.getAttributes().get("apiHeader");
        if (!apiHeader.isValid()) {
            session.close();
        }

        // 实际上用户ID应根据token来获得，这里为了方便测试直接让客户端传入一个
        WebSocketSessionManager.openSession(apiHeader.getUserId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ApiHeader apiHeader = (ApiHeader) session.getAttributes().get("apiHeader");
        WebSocketSessionManager.closeSession(apiHeader.getUserId(), session);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        //
    }
}
