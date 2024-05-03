package com.example.springsecurity.repository;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order,Long> {


    @Query(value = "SELECT op.product_id, SUM(op.quantity) AS total_sold, p.name AS product_name " +
            "FROM order_product_quantity op " +
            "JOIN order_test o ON op.order_id = o.id " +
            "JOIN product_test p ON op.product_id = p.id " +
            "WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
            "GROUP BY op.product_id, product_name", nativeQuery = true)
    List<Object[]> countSoldProductsByYear(@Param("year") int year);

//    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);




    @Query(value = "SELECT SUM(CASE WHEN p.discount_price IS NOT NULL THEN op.quantity * p.discount_price ELSE op.quantity * p.price END) " +
            "FROM order_product_quantity op " +
            "JOIN order_test o ON op.order_id = o.id " +
            "JOIN product_test p ON op.product_id = p.id " +
            "WHERE EXTRACT(YEAR FROM o.created_at) = :year", nativeQuery = true)
    BigDecimal getTotalRevenueByYear(int year);


    @Query(value = "SELECT EXTRACT(YEAR FROM o.created_at) AS year, SUM(op.quantity) AS totalSold " +
            "FROM order_test o " +
            "JOIN order_product_quantity op ON o.id = op.order_id " +
            "GROUP BY EXTRACT(YEAR FROM o.created_at)", nativeQuery = true)
    List<Object[]> getProductSalesStatistics();



    @Query(value = "SELECT " +
            "   EXTRACT(YEAR FROM o.created_at) AS year, " +
            "   SUM(opq.quantity * COALESCE(p.discount_price, p.price)) AS totalRevenue " +
            "FROM " +
            "   order_test o " +
            "JOIN " +
            "   order_product_quantity opq ON o.id = opq.order_id " +
            "JOIN " +
            "   product_test p ON opq.product_id = p.id " +
            "GROUP BY " +
            "   EXTRACT(YEAR FROM o.created_at) " +
            "ORDER BY " +
            "   year ASC",
            nativeQuery = true)
    List<Object[]> getSoldProductSalesStatistics();

    List<Order> findAllByCustomer_Id(Long customerId);


}
