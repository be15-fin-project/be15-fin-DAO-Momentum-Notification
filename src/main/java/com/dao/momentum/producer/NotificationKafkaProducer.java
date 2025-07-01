package com.dao.momentum.producer;

import com.dao.momentum.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${custom.kafka.notification-topic}")
    private String topic;

    public void sendNotification(String userId, NotificationMessage message) {
        kafkaTemplate.send(topic, userId, message);
        log.info("[Kafka] 유저 {} 에게 알림 전송 완료 (Kafka Topic: {})", userId, topic);
    }
}

