package com.example.springsecurity.repository;

import com.example.springsecurity.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserRole ur SET ur.isDeleted = true WHERE ur.user = :user")
    void deleteByUser(@Param("user") User user);
}
