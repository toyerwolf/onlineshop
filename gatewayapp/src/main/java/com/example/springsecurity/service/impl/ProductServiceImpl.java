package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.ProductMapper;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.ProductRequest;
import com.example.springsecurity.service.ImageService;
import com.example.springsecurity.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ImageService imageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    private final ProductMapper productMapper=ProductMapper.INSTANCE;

   @Value("${image-url-prefix}")
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
    private static ProductDto getProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());
        productDto.setDeleted(product.isDeleted());
//        productDto.setCategoryId(product.getCategory().getCategoryId());
        return productDto;
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }



}



