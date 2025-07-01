package com.dao.momentum.redis;

import com.dao.momentum.dto.NotificationMessage;
import com.dao.momentum.service.NotificationService;
import com.dao.momentum.sse.SseEmitterManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final SseEmitterManager emitterManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = message.toString();
            log.info("[RedisSubscriber] 수신된 메시지: {}", msgBody);

            NotificationMessage notification = objectMapper.readValue(msgBody, NotificationMessage.class);
            String userId = String.valueOf(notification.getReceiverId());

            // 1. 알림 DB 저장
            notificationService.save(notification);
            log.info("[RedisSubscriber] DB 저장 완료: 수신자={}", userId);

            // 2. SSE 전송 시도
            boolean sent = emitterManager.send(userId, notification);
            log.info("[RedisSubscriber] SSE 전송 {}: 수신자={}", sent ? "성공" : "실패", userId);

        } catch (Exception e) {
            log.error("[RedisSubscriber] 알림 처리 중 오류", e);
        }
    }
}
