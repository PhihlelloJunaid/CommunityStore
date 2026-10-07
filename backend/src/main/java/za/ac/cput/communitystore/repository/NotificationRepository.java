package za.ac.cput.communitystore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.communitystore.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);
}