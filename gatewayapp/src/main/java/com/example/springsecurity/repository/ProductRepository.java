package com.example.springsecurity.repository;

import com.example.springsecurity.dto.SoldProductsResponse;
import com.example.springsecurity.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {


    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.categoryId = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE lower(p.name) LIKE lower(concat('%', :keyword, '%'))")
    List<Product> searchProductByName(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.orderProducts op WHERE op.order.id = :orderId")
    List<Product> findProductsByOrderId(Long orderId);

    @Query(value = "SELECT op.product_id, SUM(op.quantity) AS total_sold, p.name AS product_name " +
            "FROM order_product op " +
            "JOIN order_test o ON op.order_id = o.id " +
            "JOIN product_test p ON op.product_id = p.id " +
            "WHERE o.created_at BETWEEN :startDate AND :endDate " +
            "AND o.status = 'PAID' " +
            "GROUP BY p.name, op.product_id", nativeQuery = true)
    List<Object[]> countSoldProductsByYear(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT o.created_at AS sales_year, SUM(op.quantity) AS totalSold " +
            "FROM order_test o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "WHERE o.status = 'PAID' " +
            "GROUP BY o.created_at", nativeQuery = true)
    List<Object[]> getProductSalesStatistics();



    @Query(value = "SELECT " +
            "   o.created_at AS saleYear, " +
            "   SUM(opq.quantity * COALESCE(p.discount_price, p.price)) AS totalRevenue " +
            "FROM " +
            "   order_test o " +
            "JOIN " +
            "   order_product opq ON o.id = opq.order_id " +
            "JOIN " +
            "   product_test p ON opq.product_id = p.id " +
            "WHERE " +
            "   o.status = 'PAID' " + // условие на статус заказа
            "GROUP BY " +
            "   o.created_at " +
            "ORDER BY " +
            "   saleYear ASC",
            nativeQuery = true)
    List<Object[]> getSoldProductSalesStatistics();



}
