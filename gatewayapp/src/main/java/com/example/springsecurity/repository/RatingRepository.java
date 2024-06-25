package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

    Optional<Rating> findByProductIdAndCustomerId(Long productId, Long customerId);
}
