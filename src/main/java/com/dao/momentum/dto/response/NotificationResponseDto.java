package com.dao.momentum.dto.response;

import com.dao.momentum.entity.IsRead;
import com.dao.momentum.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private final Long notificationId;
    private final Long empId;
    private final Long approveId;
    private final Long contactId;
    private final String content;
    private final String url;
    private final IsRead isRead;
    private final LocalDateTime createdAt;

    @Builder
    public NotificationResponseDto(Long notificationId, Long empId, Long approveId, Long contactId,
                                   String content, String url, IsRead isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.empId = empId;
        this.approveId = approveId;
        this.contactId = contactId;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .empId(notification.getEmpId())
                .approveId(notification.getApproveId())
                .contactId(notification.getContactId())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
