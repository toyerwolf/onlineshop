package com.example.springsecurity.service;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.req.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    void addProductToCategory(ProductRequest productRequest, Long categoryId, MultipartFile image) throws IOException;

    List<ProductDto> findProductsByCategoryId(Long categoryId);

    ProductDto getProductById(Long productId);

    void updateProduct(Long productId, ProductRequest productRequest);
    void deleteProduct(Long productId);

    Page<ProductDto> getAllProducts(int page,int pageSize);

    List<ProductDto> searchProductByName(String keyword);

   void decreaseCount(Long productId, int quantity);

     Product findProductById(Long productId);
     List<ProductDto> findProductsByOrderId(Long orderId);
}
