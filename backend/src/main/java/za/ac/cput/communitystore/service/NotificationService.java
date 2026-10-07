package za.ac.cput.communitystore.service;

import java.util.List;
import za.ac.cput.communitystore.entity.Notification;
import za.ac.cput.communitystore.enums.NotificationType;

public interface NotificationService {

    List<Notification> getUserNotifications(Long userId);

    long getUnreadCount(Long userId);

    Notification markAsRead(Long id, Long userId);

    Notification createNotification(
            Long userId,
            String title,
            String message,
            NotificationType type
    );
}