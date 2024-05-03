package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.User;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {


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
}
