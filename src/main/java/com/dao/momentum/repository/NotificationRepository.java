package com.dao.momentum.repository;

import com.dao.momentum.entity.IsRead;
import com.dao.momentum.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n " +
            "WHERE n.empId = :empId AND n.createdAt >= :fromDate " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findRecentByEmpId(@Param("empId") long empId,
                                         @Param("fromDate") LocalDateTime fromDate);

    List<Notification> findByEmpIdAndIsRead(Long empId, IsRead isRead);
}
