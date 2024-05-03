package com.example.springsecurity.repository;

import com.example.springsecurity.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {

    List<OrderProduct> findOrderProductsByOrderId(Long orderId);
}
