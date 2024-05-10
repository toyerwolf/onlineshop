package com.example.springsecurity.repository;

import com.example.springsecurity.entity.CustomerCardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCardDetailsRepository extends JpaRepository<CustomerCardDetails,Long> {

    Optional<CustomerCardDetails> findByCardNumber(String cardNumber);
}
