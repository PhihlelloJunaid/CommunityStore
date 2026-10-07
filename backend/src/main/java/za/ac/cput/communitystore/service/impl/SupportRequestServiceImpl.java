package za.ac.cput.communitystore.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import za.ac.cput.communitystore.dto.SupportRequestCreateRequest;
import za.ac.cput.communitystore.dto.SupportRequestUpdateRequest;
import za.ac.cput.communitystore.entity.SupportRequest;
import za.ac.cput.communitystore.enums.NotificationType;
import za.ac.cput.communitystore.enums.SupportRequestStatus;
import za.ac.cput.communitystore.repository.SupportRequestRepository;
import za.ac.cput.communitystore.service.NotificationService;
import za.ac.cput.communitystore.service.SupportRequestService;

@Service
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository repository;
    private final NotificationService notificationService;

    public SupportRequestServiceImpl(
            SupportRequestRepository repository,
            NotificationService notificationService
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public SupportRequest create(
            Long customerId,
            SupportRequestCreateRequest request
    ) {
        SupportRequest supportRequest = new SupportRequest();

        supportRequest.setCustomerId(customerId);
        supportRequest.setOrderId(request.orderId());
        supportRequest.setSubject(request.subject());
        supportRequest.setDescription(request.description());
        supportRequest.setStatus(SupportRequestStatus.OPEN);
        supportRequest.setResponse(null);
        supportRequest.setCreatedAt(LocalDateTime.now());
        supportRequest.setUpdatedAt(LocalDateTime.now());

        return repository.save(supportRequest);
    }

    @Override
    public List<SupportRequest> getCustomerRequests(Long customerId) {
        return repository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Override
    public List<SupportRequest> getAllRequests() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public SupportRequest update(
            Long id,
            SupportRequestUpdateRequest request
    ) {
        SupportRequest supportRequest = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Support request not found"));

        supportRequest.setStatus(request.status());
        supportRequest.setResponse(request.response());
        supportRequest.setUpdatedAt(LocalDateTime.now());

        SupportRequest savedRequest = repository.save(supportRequest);

        notificationService.createNotification(
                supportRequest.getCustomerId(),
                "Support request updated",
                "Your support request \"" +
                        supportRequest.getSubject() +
                        "\" has been updated.",
                NotificationType.SUPPORT
        );

        return savedRequest;
    }
}