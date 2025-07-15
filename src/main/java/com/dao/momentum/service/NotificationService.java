package com.dao.momentum.service;

import com.dao.momentum.dto.NotificationMessage;
import com.dao.momentum.dto.response.NotificationResponseDto;
import com.dao.momentum.entity.IsRead;
import com.dao.momentum.entity.Notification;
import com.dao.momentum.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationMessage save(NotificationMessage message) {
        Notification notification = Notification.builder()
                .empId(message.getReceiverId())
                .approveId(message.getApproveId())
                .contactId(message.getContactId())
                .content(message.getContent())
                .url(message.getUrl())
                .isRead(IsRead.N)
                .createdAt(message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // 저장된 ID를 message 객체에 세팅하여 반환
        message.setId(saved.getNotificationId());
        return message;
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(Long empId) {
        List<Notification> unreadNotifications = notificationRepository.findByEmpIdAndIsRead(empId, IsRead.N);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUserNotifications(long empId) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<Notification> notifications = notificationRepository.findRecentByEmpId(empId, oneMonthAgo);
        return notifications.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }
}