package com.example.springsecurity.repository;

import com.example.springsecurity.entity.User;

import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = "customer")
    Optional<User> findByUsername(String username);


}
