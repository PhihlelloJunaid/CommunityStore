package za.ac.cput.communitystore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import za.ac.cput.communitystore.entity.Notification;
import za.ac.cput.communitystore.enums.NotificationType;
import za.ac.cput.communitystore.repository.NotificationRepository;
import za.ac.cput.communitystore.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public Notification markAsRead(Long id, Long userId) {
        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        notification.setRead(true);
        return repository.save(notification);
    }

    @Override
    public Notification createNotification(
            Long userId,
            String title,
            String message,
            NotificationType type
    ) {
        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return repository.save(notification);
    }
}