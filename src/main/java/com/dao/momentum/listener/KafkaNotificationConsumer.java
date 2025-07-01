package com.dao.momentum.listener;

import com.dao.momentum.dto.NotificationMessage;
import com.dao.momentum.redis.RedisPublisher;
import com.dao.momentum.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaNotificationConsumer {

    private final SseEmitterManager sseEmitterManager;
    private final RedisPublisher redisPublisher;

    @KafkaListener(
            topics = "${custom.kafka.notification-topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            ConsumerRecord<String, NotificationMessage> record,
            Acknowledgment ack
    ) {
        String userId = record.key();
        NotificationMessage message = record.value();

        log.info("[Kafka] 유저 {} 에게 도착한 알림 처리 시작", userId);

        boolean sent = sseEmitterManager.send(userId, message);

        if (sent) {
            ack.acknowledge();                   // Kafka 커밋
            log.info("[Kafka] 유저 {} 에게 알림 전송 및 커밋 완료", userId);
        } else {
            redisPublisher.publish(message);
            log.warn("[Kafka] 유저 {} 는 SSE 연결이 없어 Redis로 fan-out만 수행 (커밋 보류)", userId);
        }
    }
}

