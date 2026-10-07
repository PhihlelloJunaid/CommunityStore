package za.ac.cput.communitystore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import za.ac.cput.communitystore.dto.ProfileUpdateRequest;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.enums.Role;
import za.ac.cput.communitystore.repository.UserRepository;
import za.ac.cput.communitystore.service.UserService;
import za.ac.cput.communitystore.util.UserIdGenerator;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (userRepository.existsByEmail(user.getEmail().trim())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setEmail(user.getEmail().trim());

        user.setRole(Role.CUSTOMER);

        user.setStudentNumber(
                generateUniqueUserNumber(Role.CUSTOMER)
        );

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByStudentNumber(String studentNumber) {
        return userRepository.findByStudentNumber(studentNumber);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (updatedUser.getFirstName() != null &&
                !updatedUser.getFirstName().isBlank()) {

            existingUser.setFirstName(
                    updatedUser.getFirstName().trim()
            );
        }

        if (updatedUser.getLastName() != null &&
                !updatedUser.getLastName().isBlank()) {

            existingUser.setLastName(
                    updatedUser.getLastName().trim()
            );
        }

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().isBlank()) {

            String newEmail = updatedUser.getEmail().trim();

            userRepository.findByEmail(newEmail)
                    .filter(existing ->
                            !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Email already exists"
                        );
                    });

            existingUser.setEmail(newEmail);
        }

        if (updatedUser.getPhoneNumber() != null &&
                !updatedUser.getPhoneNumber().isBlank()) {

            existingUser.setPhoneNumber(
                    updatedUser.getPhoneNumber().trim()
            );
        }

        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User updateProfile(Long id, ProfileUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (request.firstName() == null ||
                request.firstName().isBlank()) {

            throw new IllegalArgumentException(
                    "First name is required"
            );
        }

        if (request.lastName() == null ||
                request.lastName().isBlank()) {

            throw new IllegalArgumentException(
                    "Last name is required"
            );
        }

        if (request.email() == null ||
                request.email().isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (request.phoneNumber() == null ||
                request.phoneNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number is required"
            );
        }

        String email = request.email().trim();

        userRepository.findByEmail(email)
                .filter(existing ->
                        !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Email already exists"
                    );
                });

        user.setFirstName(
                request.firstName().trim()
        );

        user.setLastName(
                request.lastName().trim()
        );

        user.setEmail(email);

        user.setPhoneNumber(
                request.phoneNumber().trim()
        );

        if (request.password() != null &&
                !request.password().isBlank()) {

            if (request.password().length() < 8) {
                throw new IllegalArgumentException(
                        "Password must contain at least 8 characters"
                );
            }

            user.setPassword(
                    passwordEncoder.encode(
                            request.password()
                    )
            );
        }

        return userRepository.save(user);
    }

    @Override
    public User updateRole(Long id, Role role) {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Role is required"
            );
        }

        if (role == Role.ADMIN) {
            throw new IllegalArgumentException(
                    "ADMIN accounts are developer-managed"
            );
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException(
                    "ADMIN accounts are developer-managed"
            );
        }

        Role oldRole = user.getRole();

        user.setRole(role);

        if (oldRole != role) {
            user.setStudentNumber(
                    generateUniqueUserNumber(role)
            );
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "User not found"
            );
        }

        userRepository.deleteById(id);
    }

    private String generateUniqueUserNumber(Role role) {

        String number;

        do {
            if (role == Role.CUSTOMER) {
                number = UserIdGenerator.customerId();
            } else if (role == Role.CUSTOMER_SUPPORT) {
                number = UserIdGenerator.staffId("SUP");
            } else if (role == Role.STORE_EMPLOYEE) {
                number = UserIdGenerator.staffId("EMP");
            } else {
                number = UserIdGenerator.staffId("USR");
            }
        } while (
                userRepository.findByStudentNumber(number).isPresent()
        );

        return number;
    }
}