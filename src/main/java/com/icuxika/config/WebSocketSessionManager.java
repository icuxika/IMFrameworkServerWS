package com.icuxika.config;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {

    /**
     * 用户ID <-> 多个设备登录的会话信息
     */
    private static final Map<Long, List<ManageableWebSocketSession>> USER_SESSION_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 会话id <-> 用户ID
     */
    private static final Map<String, ManageableWebSocketSession> SESSION_ID_MAP = new ConcurrentHashMap<>();

    /**
     * 新的用户会话
     *
     * @param userId           用户ID
     * @param webSocketSession 会话数据
     */
    public static void openSession(long userId, WebSocketSession webSocketSession) {
        // 判断当前是否记录了该用户的会话数据
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.computeIfAbsent(userId, k -> new ArrayList<>());
        ManageableWebSocketSession manageableWebSocketSession = new ManageableWebSocketSession(webSocketSession, userId);
        SESSION_ID_MAP.put(webSocketSession.getId(), manageableWebSocketSession);
        userSessionList.add(manageableWebSocketSession);

        System.out.println(userId + "上线了");

        // TODO 同类型设备登录同账号是否踢掉、向自己的某个设备发送消息
    }

    /**
     * 关闭用户会话
     *
     * @param userId           用户ID
     * @param webSocketSession 被关闭的会话
     */
    public static void closeSession(long userId, WebSocketSession webSocketSession) {
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.get(userId);
        if (userSessionList != null) {
            ManageableWebSocketSession manageableWebSocketSession = SESSION_ID_MAP.get(webSocketSession.getId());
            if (manageableWebSocketSession != null) {
                userSessionList.remove(manageableWebSocketSession);
                System.out.println(userId + "下线了");
            }
        }
    }

    /**
     * 获取当前的会话集合
     *
     * @return 会话集合
     */
    public static List<ManageableWebSocketSession> getCurrentWebSocketSessionList() {
        List<ManageableWebSocketSession> result = new ArrayList<>();
        USER_SESSION_LIST_MAP.values().forEach(result::addAll);
        return result;
    }

    /**
     * 向指定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    public static void sendMessageToUser(long userId, String message) {
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.get(userId);
        if (userSessionList != null && !userSessionList.isEmpty()) {
            userSessionList.forEach(manageableWebSocketSession -> manageableWebSocketSession.sendMessage(message));
        }
    }
}
