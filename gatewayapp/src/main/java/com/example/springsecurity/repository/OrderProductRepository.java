package com.example.springsecurity.repository;

import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {
    @EntityGraph(attributePaths = {"product"})
    List<OrderProduct> findOrderProductsByOrderId(Long orderId);


}
