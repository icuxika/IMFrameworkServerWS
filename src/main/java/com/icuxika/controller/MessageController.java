package com.icuxika.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.config.WebSocketSessionManager;
import com.icuxika.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送消息
     *
     * @param message 消息
     */
    @PostMapping("sendMessage")
    public void sendMessage(@RequestBody Message message) {
        try {
            WebSocketSessionManager.sendMessageToUser(message.getReceiverId(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
