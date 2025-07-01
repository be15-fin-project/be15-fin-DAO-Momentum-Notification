package com.dao.momentum.redis;

import com.dao.momentum.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {

    private final RedisTemplate<String, NotificationMessage> notificationMessageRedisTemplate;
    private static final String CHANNEL = "notification-channel";

    public void publish(NotificationMessage message) {
        notificationMessageRedisTemplate.convertAndSend(CHANNEL, message);
    }
}
