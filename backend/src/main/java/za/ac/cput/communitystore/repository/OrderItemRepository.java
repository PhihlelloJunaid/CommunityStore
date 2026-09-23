package za.ac.cput.communitystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.communitystore.entity.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}