package com.dao.momentum.listener;

import com.dao.momentum.dto.NotificationMessage;
import com.dao.momentum.redis.RedisPublisher;
import com.dao.momentum.service.NotificationService;
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
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${custom.kafka.general-notification-topic}",
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
            notificationService.save(message);
            log.info("[Kafka] DB 저장 완료: 수신자={}", userId);
            ack.acknowledge();                   // Kafka 커밋
            log.info("[Kafka] 유저 {} 에게 알림 전송 및 커밋 완료", userId);
        } else {
            try {
                redisPublisher.publish(message);
                ack.acknowledge();
                log.warn("[Kafka] 유저 {} 는 SSE 미연결로 Redis로 fan-out만 수행 (커밋 완료)", userId);
            } catch (Exception e) {
                log.error("[Kafka] Redis publish 실패 - 커밋 보류: 수신자={}", userId, e);
                // ack 생략 -> Kafka가 재전송하도록 유도
            }
        }
    }

    @KafkaListener(
            topics = "${custom.kafka.evaluation-notification-topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenEvaluationNotification(
            ConsumerRecord<String, NotificationMessage> record,
            Acknowledgment ack
    ) {
        String userId = record.key();
        NotificationMessage message = record.value();

        log.info("[Kafka - 평가알림] 유저 {} 에게 도착한 알림 처리 시작", userId);

        boolean sent = sseEmitterManager.send(userId, message);

        if (sent) {
            notificationService.save(message);
            log.info("[Kafka - 평가알림] DB 저장 완료: 수신자={}", userId);
            ack.acknowledge();
            log.info("[Kafka - 평가알림] 알림 전송 및 커밋 완료: 수신자={}", userId);
        } else {
            redisPublisher.publish(message);
            ack.acknowledge();
            log.warn("[Kafka - 평가알림] SSE 미연결로 Redis로 fan-out: 수신자={}", userId);
        }
    }
}

