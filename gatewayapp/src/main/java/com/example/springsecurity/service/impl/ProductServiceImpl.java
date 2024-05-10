package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.ProductMapper;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.ProductRequest;
import com.example.springsecurity.service.ImageService;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final OrderRepository orderRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ImageService imageService, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.orderRepository = orderRepository;
    }

    private final ProductMapper productMapper=ProductMapper.INSTANCE;

   @Value("${image-url-prefix}")
   @Setter
   private String imageUrlPrefix;

    @Override
    @Transactional
    public void addProductToCategory(ProductRequest productRequest, Long categoryId, MultipartFile image) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDiscountPrice(productRequest.getDiscountPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setDeleted(false);
        String imageName = imageService.saveImage(image);
        product.setImageUrl(imageName);
        productRepository.save(product);
        category.addProduct(product);
        categoryRepository.save(category);
    }

    @Override
    public List<ProductDto> findProductsByCategoryId(Long categoryId) {
        List<Product> products=productRepository.findProductsByCategoryIdWithCategory(categoryId);
        if (products.isEmpty()) {
            throw new NotFoundException("Products not found for category id: " + categoryId);
        }
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = getProductDto(product);
            String imageUrl = imageUrlPrefix + product.getImageUrl();
            productDto.setImageUrl(imageUrl);
            productDtos.add(productDto);
        }

        return productDtos;
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with " + productId + " not found"));
        String imageUrl = imageUrlPrefix + product.getImageUrl();
        ProductDto productDto = productMapper.toDto(product);
        productDto.setImageUrl(imageUrl);

        return productDto;
    }


    @Transactional
    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with " + productId + " notfound"));
        productMapper.updateProductFromRequest(productRequest, product);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with  " + productId +  " not found" ));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page - 1, pageSize);
        Page<Product> products = productRepository.findAll(pageRequest);
        List<ProductDto> productDtos = productMapper.toDtoList(products.getContent());
        for (ProductDto productDto : productDtos) {
            String staticUrl = imageUrlPrefix + productDto.getImageUrl();
            productDto.setImageUrl(staticUrl);
        }
        return new PageImpl<>(productDtos, pageRequest, products.getTotalElements());
    }



    @Override
    public List<ProductDto> searchProductByName(String keyword) {
        List<Product> products = productRepository.searchProductByName(keyword);
        return products.stream()
                .map(ProductMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void decreaseCount(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        int currentQuantity = product.getQuantity();
        if (currentQuantity < quantity) {
            throw new InsufficientQuantityException("Insufficient quantity");
        }
        int newQuantity = currentQuantity - quantity;
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }



    @NotNull
    static ProductDto getProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());
        productDto.setDeleted(product.isDeleted());
        productDto.setImageUrl(product.getImageUrl());
//        productDto.setCategoryId(product.getCategory().getCategoryId());
        return productDto;
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public List<ProductDto> findProductsByOrderId(Long orderId) {
        List<Product> products = productRepository.findProductsByOrderId(orderId);
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> countSoldProductsByYear(int year) {
        List<Object[]> results = productRepository.countSoldProductsByYear(year);
        Map<String, Integer> soldProductsByYear = new HashMap<>();
        for (Object[] result : results) {
            String productName = (String) result[2];
            Integer totalSold = ((Number) result[1]).intValue();
            soldProductsByYear.put(productName, totalSold);
        }
        return soldProductsByYear;
    }


    @Override
    public Map<Integer, Integer> getProductSalesStatistics() {
        List<Object[]> salesData = productRepository.getProductSalesStatistics();
        Map<Integer, Integer> salesByYear = new HashMap<>();
        for (Object[] row : salesData) {
            int year = ((Number) row[0]).intValue();
            int totalSold = ((Number) row[1]).intValue();
            salesByYear.put(year, totalSold);
        }
        return salesByYear;
    }


    @Override
    public Map<Integer, BigDecimal> getTotalProductSalesRevenueByYear() {
        List<Object[]> salesData = productRepository.getSoldProductSalesStatistics();
        Map<Integer, BigDecimal> totalRevenueByYear = new HashMap<>();
        for (Object[] row : salesData) {
            int year = ((Number) row[0]).intValue();
            BigDecimal totalRevenue = (BigDecimal) row[1];
            totalRevenueByYear.put(year, totalRevenue);
        }
        return totalRevenueByYear;
    }



}



