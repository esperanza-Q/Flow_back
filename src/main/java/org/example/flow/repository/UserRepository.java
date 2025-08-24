package org.example.flow.repository;

import org.example.flow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserId(Long userId);
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
}
