package com.dao.momentum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notificationId;

    private long empId;

    private Long approveId;

    private Long contactId;

    private String content;

    private String url;

    @Enumerated(EnumType.STRING)
    private IsRead isRead;

    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = IsRead.Y;
    }
}
