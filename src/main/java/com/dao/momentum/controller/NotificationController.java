package com.dao.momentum.controller;

import com.dao.momentum.entity.Notification;
import com.dao.momentum.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping("/{empId}")
    public List<Notification> getNotifications(@PathVariable long empId) {
        return notificationService.getUserNotifications(empId);
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("알림 읽음 처리 완료");
    }
}
