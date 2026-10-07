package za.ac.cput.communitystore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.ac.cput.communitystore.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentNumber(String studentNumber);

    boolean existsByEmail(String email);

    boolean existsByStudentNumber(String studentNumber);
}