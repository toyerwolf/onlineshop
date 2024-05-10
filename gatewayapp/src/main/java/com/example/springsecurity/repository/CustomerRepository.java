package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.User;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.control.MappingControl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {


    @EntityGraph(attributePaths = {"user", "address"})
    @NotNull
    Page<Customer> findAll(@NotNull Pageable pageable);


    //Object potomu chto vozrashayets dva znacheniya
    @Query(value = "SELECT " +
            "   EXTRACT(YEAR FROM c.registered_at) AS year, " +
            "   COUNT(*) AS customer_registrations " +
            "FROM " +
            "   customer c " +
            "GROUP BY " +
            "   EXTRACT(YEAR FROM c.registered_at) " +
            "ORDER BY " +
            "   year ASC",
            nativeQuery = true)
    List<Object[]> getCustomerRegistrationsByYear();

//Lower nujen dlya preobrozaniya stroki v nujniy registr
    //etot query budet iskat nezavisimo v poiske pishesh s bolshoy bukvi ili s malenkoy
@Query(value = "SELECT * FROM customer WHERE name LIKE :keyword OR LOWER(name) LIKE LOWER(CONCAT(:keyword, '%')) OR LOWER(surname) LIKE LOWER(CONCAT(:keyword, '%')) OR LOWER(address) LIKE LOWER(CONCAT(:keyword, '%'))", nativeQuery = true)
    List<Customer> searchCustomers(String keyword);


}
