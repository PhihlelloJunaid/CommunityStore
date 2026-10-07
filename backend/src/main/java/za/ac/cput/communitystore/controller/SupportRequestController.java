package za.ac.cput.communitystore.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.communitystore.dto.SupportRequestCreateRequest;
import za.ac.cput.communitystore.dto.SupportRequestUpdateRequest;
import za.ac.cput.communitystore.entity.SupportRequest;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.repository.UserRepository;
import za.ac.cput.communitystore.service.SupportRequestService;

@RestController
@RequestMapping("/api/support/requests")
public class SupportRequestController {

    private final SupportRequestService service;
    private final UserRepository userRepository;

    public SupportRequestController(
            SupportRequestService service,
            UserRepository userRepository
    ) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<SupportRequest> create(
            Authentication authentication,
            @Valid @RequestBody SupportRequestCreateRequest request
    ) {
        User user = getCurrentUser(authentication);

        SupportRequest created =
                service.create(user.getId(), request);

        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<SupportRequest>> getRequests(
            Authentication authentication
    ) {
        User user = getCurrentUser(authentication);

        boolean staff = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_CUSTOMER_SUPPORT") ||
                                authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (staff) {
            return ResponseEntity.ok(service.getAllRequests());
        }

        return ResponseEntity.ok(
                service.getCustomerRequests(user.getId())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT','ADMIN')")
    public ResponseEntity<SupportRequest> update(
            @PathVariable Long id,
            @Valid @RequestBody SupportRequestUpdateRequest request
    ) {
        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}