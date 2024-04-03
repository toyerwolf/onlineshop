package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.entity.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE UserRole ur SET ur.isDeleted = true WHERE ur.user = :user")
    void deleteByUser(@Param("user") User user);

    @Transactional
    @Modifying
    @Query("UPDATE UserRole ur SET ur.isDeleted = true WHERE ur.role = :role")
    void deleteByRole(@Param("role") Role role);
}
