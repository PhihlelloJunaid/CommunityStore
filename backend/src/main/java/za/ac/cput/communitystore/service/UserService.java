package za.ac.cput.communitystore.service;

import java.util.List;
import java.util.Optional;
import za.ac.cput.communitystore.entity.User;

public interface UserService {
    User register(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentNumber(String studentNumber);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User updateUser(Long id, User updatedUser);

    void deleteUser(Long id);
}