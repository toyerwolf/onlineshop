package com.example.springsecurity.service;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.req.ProductRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {

    void addProductToCategory(ProductRequest productRequest, Long categoryId, MultipartFile image);

    ProductDtoContainer findProductsByCategoryId(Long categoryId);

    ProductDto getProductById(Long productId);

    void updateProduct(Long productId, ProductRequest productRequest);
    void deleteProduct(Long productId);

    Page<ProductDto> getAllProducts(int page,int pageSize);

    List<ProductDto> searchProductByName(String keyword);

   void decreaseCount(Long productId, int quantity);

//     Product findProductById(Long productId);
     List<ProductDto> findProductsByOrderId(Long orderId);
    ProductSalesResponseDto countSoldProductsByYear(int year);

    YearlySalesResponseDto getProductSalesStatistics();

    YearlySalesRevenueResponseDTO getTotalProductSalesRevenueByYear();

    void applyNewYearDiscount();

    void removeNewYearDiscount();

    Integer getProductRating(Long productId);






}
