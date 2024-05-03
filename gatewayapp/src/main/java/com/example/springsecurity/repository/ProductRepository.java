package com.example.springsecurity.repository;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {


    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.categoryId = :categoryId")
    List<Product> findProductsByCategoryIdWithCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE lower(p.name) LIKE lower(concat('%', :keyword, '%'))")
    List<Product> searchProductByName(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.orderProducts op WHERE op.order.id = :orderId")
    List<Product> findProductsByOrderId(Long orderId);


}
