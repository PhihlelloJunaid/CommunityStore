package za.ac.cput.communitystore.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import za.ac.cput.communitystore.dto.AuthResponse;
import za.ac.cput.communitystore.dto.LoginRequest;
import za.ac.cput.communitystore.dto.RegisterRequest;
import za.ac.cput.communitystore.dto.UserResponse;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.enums.NotificationType;
import za.ac.cput.communitystore.enums.Role;
import za.ac.cput.communitystore.service.NotificationService;
import za.ac.cput.communitystore.service.UserService;
import za.ac.cput.communitystore.util.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            NotificationService notificationService
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim())
                .phoneNumber(request.getPhoneNumber().trim())
                .password(request.getPassword())
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userService.register(user);

        notificationService.createNotification(
                savedUser.getId(),
                "Welcome to Community Store",
                "Your account is ready. Browse pre-loved items and make your first order.",
                NotificationType.SYSTEM
        );

        String token = jwtService.generateToken(savedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new AuthResponse(
                                token,
                                toResponse(savedUser)
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail().trim(),
                                request.getPassword()
                        )
                );

        if (!authentication.isAuthenticated()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        User user = userService
                .findByEmail(request.getEmail().trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        toResponse(user)
                )
        );
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getStudentNumber(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}