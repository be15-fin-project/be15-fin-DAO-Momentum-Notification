package com.dao.momentum.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationMessage {
    private Long id;
    private String content;
    private String type;
    private String url;
    private Long receiverId;
    private Long senderId;
    private String senderName;
    private LocalDateTime timestamp;
    private Long approveId;
    private Long contactId;
}
