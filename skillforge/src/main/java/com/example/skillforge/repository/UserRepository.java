package com.example.skillforge.repository;

import com.example.skillforge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;  // ← manquant
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET avatar_data = :avatarData, avatar_url = null WHERE user_id = :id", nativeQuery = true)
    void updateAvatarData(@Param("id") Long id, @Param("avatarData") String avatarData);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET avatar_url = :avatarUrl, avatar_data = null WHERE user_id = :id", nativeQuery = true)
    void updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);
}