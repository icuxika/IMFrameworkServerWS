package com.icuxika.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionDeathDetectionRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> {
            // 开始会话死亡定时检测任务
            while (true) {
                List<ManageableWebSocketSession> userSessionList = WebSocketSessionManager.getCurrentWebSocketSessionList();
                if (userSessionList.isEmpty()) {
                    //
                } else {
                    userSessionList.forEach(manageableWebSocketSession -> {
                        if (!manageableWebSocketSession.sendPing()) {
                            WebSocketSessionManager.closeSession(manageableWebSocketSession.getUserId(), manageableWebSocketSession.getWebSocketSession());
                            System.out.println(manageableWebSocketSession.getUserId() + "超时下线");
                        }
                    });
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
