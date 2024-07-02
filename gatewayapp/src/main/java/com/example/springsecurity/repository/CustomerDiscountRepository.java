package com.example.springsecurity.repository;

import com.example.springsecurity.entity.CustomerDiscount;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDiscountRepository  extends JpaRepository<CustomerDiscount,Long> {

    @Query(value = "SELECT * FROM customer_discount WHERE customer_id = :customerId AND product_id = :productId", nativeQuery = true)
    CustomerDiscount findByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);


    CustomerDiscount findFirstByCustomerIdAndDiscountPriceNotNull(Long customerId);
}
