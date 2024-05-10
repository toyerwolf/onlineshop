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

    @Query(value = "SELECT op.product_id, SUM(op.quantity) AS total_sold, p.name AS product_name " +
            "FROM order_product op " +
            "JOIN order_test o ON op.order_id = o.id " +
            "JOIN product_test p ON op.product_id = p.id " +
            "WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
            "AND o.status = 'PAID' " +
            "GROUP BY p.name, op.product_id", nativeQuery = true)
    List<Object[]> countSoldProductsByYear(@Param("year") int year);

    @Query(value = "SELECT EXTRACT(YEAR FROM o.created_at) AS year, SUM(op.quantity) AS totalSold " +
            "FROM order_test o " +
            "JOIN order_product op ON o.id = op.order_id " +
            "WHERE o.status = 'PAID' " + // Добавляем условие на статус заказа
            "GROUP BY EXTRACT(YEAR FROM o.created_at)", nativeQuery = true)
    List<Object[]> getProductSalesStatistics();



    @Query(value = "SELECT " +
            "   EXTRACT(YEAR FROM o.created_at) AS year, " +
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
            "   EXTRACT(YEAR FROM o.created_at) " +
            "ORDER BY " +
            "   year ASC",
            nativeQuery = true)
    List<Object[]> getSoldProductSalesStatistics();



}
