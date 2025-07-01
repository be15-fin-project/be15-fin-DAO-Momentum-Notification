package com.dao.momentum.service;

import com.dao.momentum.dto.NotificationMessage;
import com.dao.momentum.entity.IsRead;
import com.dao.momentum.entity.Notification;
import com.dao.momentum.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void save(NotificationMessage message) {
        Notification notification = Notification.builder()
                .empId(message.getReceiverId())
                .approveId(message.getApproveId())
                .contactId(message.getContactId())
                .content(message.getContent())
                .url(message.getUrl())
                .isRead(IsRead.N)
                .createdAt(message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));
        notification.markAsRead();
    }

    public List<Notification> getUserNotifications(long empId) {
        return notificationRepository.findByEmpIdOrderByCreatedAtDesc(empId);
    }
}

