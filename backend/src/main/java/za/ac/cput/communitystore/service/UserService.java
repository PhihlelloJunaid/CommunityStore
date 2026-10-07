package za.ac.cput.communitystore.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.dto.ProfileUpdateRequest;

public interface UserService {
    User register(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentNumber(String studentNumber);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User updateUser(Long id, User updatedUser);

    User updateProfile(Long id, ProfileUpdateRequest request);

    User updateRole(Long id, za.ac.cput.communitystore.enums.Role role);

    void deleteUser(Long id);
}