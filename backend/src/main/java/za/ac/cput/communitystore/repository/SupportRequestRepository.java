package za.ac.cput.communitystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.communitystore.entity.SupportRequest;

import java.util.List;

public interface SupportRequestRepository
        extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<SupportRequest> findAllByOrderByCreatedAtDesc();
}