package com.icuxika.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class WebSocketHandler extends AbstractWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ApiHeader apiHeader = (ApiHeader) session.getAttributes().get(WebSocketHandshakeInterceptor.ATTRIBUTE_API_HEADER);
        if (!apiHeader.isValid()) {
            session.close();
        }

        // 实际上用户ID应根据token来获得，这里为了方便测试直接让客户端传入一个
        WebSocketSessionManager.openSession(session, apiHeader.getUserId(), apiHeader.getClientType());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ApiHeader apiHeader = (ApiHeader) session.getAttributes().get(WebSocketHandshakeInterceptor.ATTRIBUTE_API_HEADER);
        WebSocketSessionManager.closeSession(apiHeader.getUserId(), session);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        ManageableWebSocketSession manageableWebSocketSession = WebSocketSessionManager.getManageableWebSocketSessionBySession(session);
        if (manageableWebSocketSession != null) manageableWebSocketSession.onPong();
    }
}
