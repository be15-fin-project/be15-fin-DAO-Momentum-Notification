package com.dao.momentum.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SseHeartbeatScheduler {

    private final SseEmitterManager sseEmitterManager;

    public SseHeartbeatScheduler(SseEmitterManager sseEmitterManager) {
        this.sseEmitterManager = sseEmitterManager;
    }

    @Scheduled(fixedRate = 30000) // ⏱ 45초마다 실행
    public void sendHeartbeat() {
        sseEmitterManager.sendHeartbeatToAll();
    }
}