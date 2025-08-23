// src/main/java/org/example/flow/repository/ProfileRepository.java
package org.example.flow.repository;

import org.example.flow.entity.Profile;
import org.example.flow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // 파생쿼리로 user_id 매칭 (N+1 방지 수준은 서비스에서 접근 최소화로 해결)
    Optional<Profile> findByUser_UserId(Long userId);

    Profile findByUser(User user);

    // 필요 시: Optional<Profile> findByUser(User user);
}
