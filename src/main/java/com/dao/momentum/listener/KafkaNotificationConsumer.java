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

        try {
            // 1. DB 저장 및 ID 포함한 메시지 반환
            NotificationMessage savedMessage = notificationService.save(message);
            log.info("[Kafka] DB 저장 완료: 수신자={}, 알림ID={}", userId, savedMessage.getId());

            // 2. SSE 전송
            boolean sent = sseEmitterManager.send(userId, savedMessage);
            if (!sent) {
                redisPublisher.publish(savedMessage);
                log.warn("[Kafka] SSE 미연결 → Redis fan-out: 수신자={}", userId);
            }

            // 3. 커밋
            ack.acknowledge();
            log.info("[Kafka] 알림 처리 및 커밋 완료: 수신자={}", userId);

        } catch (Exception e) {
            log.error("[Kafka] 알림 처리 중 오류 → 커밋 보류: 수신자={}", userId, e);
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

        try {
            NotificationMessage savedMessage = notificationService.save(message);
            log.info("[Kafka - 평가알림] DB 저장 완료: 수신자={}, 알림ID={}", userId, savedMessage.getId());

            boolean sent = sseEmitterManager.send(userId, savedMessage);
            if (!sent) {
                redisPublisher.publish(savedMessage);
                log.warn("[Kafka - 평가알림] SSE 미연결 → Redis fan-out: 수신자={}", userId);
            }

            ack.acknowledge();
            log.info("[Kafka - 평가알림] 알림 처리 및 커밋 완료: 수신자={}", userId);

        } catch (Exception e) {
            log.error("[Kafka - 평가알림] 알림 처리 중 오류 → 커밋 보류: 수신자={}", userId, e);
        }
    }
}

