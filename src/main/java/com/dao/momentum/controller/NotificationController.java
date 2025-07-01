package com.dao.momentum.controller;

import com.dao.momentum.dto.response.NotificationResponseDto;
import com.dao.momentum.security.CustomUserDetails;
import com.dao.momentum.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // 로그인 사용자 알림 목록 조회
    @GetMapping
    public List<NotificationResponseDto> getNotifications(@AuthenticationPrincipal CustomUserDetails user) {
        return notificationService.getUserNotifications(user.getEmpId());
    }

    // 개별 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("알림 읽음 처리 완료");
    }

    // 로그인 사용자 전체 알림 읽음 처리
    @PatchMapping("/read/all")
    public ResponseEntity<String> markAllAsRead(@AuthenticationPrincipal CustomUserDetails user) {
        notificationService.markAllAsRead(user.getEmpId());
        return ResponseEntity.ok("전체 알림 읽음 처리 완료");
    }
}
