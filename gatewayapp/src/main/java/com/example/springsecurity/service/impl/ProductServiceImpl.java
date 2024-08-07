package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Rating;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.ProductMapper;
import com.example.springsecurity.projection.ProductSalesProjection;
import com.example.springsecurity.projection.ProductSalesProjectionByYear;
import com.example.springsecurity.projection.SalesRevenueProjection;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.ProductRequest;
import com.example.springsecurity.service.ImageService;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ProductFinderService productFinderService;
    private final CategoryFinderService categoryFinderService;

    private final Long categoryId = 1L;

    @Setter
    private BigDecimal discountPercentage = BigDecimal.valueOf(20);









    private final ProductMapper productMapper=ProductMapper.INSTANCE;

   @Value("${image-url-prefix}")
   @Setter
   private String imageUrlPrefix;



    @Override
    @Transactional
    public void addProductToCategory(ProductRequest productRequest, Long categoryId, MultipartFile image) {
        Category category = categoryFinderService.findCategoryById(categoryId);
        boolean productExists = productRepository.findByName(productRequest.getName()).isPresent();
        if (productExists) {
            throw new AlreadyExistException("Product with this name already exists." + productRequest.getName() );
        }
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
    public ProductDtoContainer findProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findProductsByCategoryId(categoryId);
        List<ProductDto> productDtos = products.stream()
                .map(this::getProductDto)
                .toList();
        ProductDtoContainer container = new ProductDtoContainer();
        container.setProducts(productDtos);
        return container;
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productFinderService.findProductById(productId);
        String imageUrl = imageUrlPrefix + product.getImageUrl();
        ProductDto productDto = productMapper.toDto(product);
        productDto.setImageUrl(imageUrl);
        return productDto;
    }


    @Transactional
    @Override
    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productFinderService.findProductById(productId);
        productMapper.updateProductFromRequest(productRequest, product);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productFinderService.findProductById(productId);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page - 1, pageSize);
        Page<Product> products = productRepository.findAll(pageRequest);
        List<ProductDto> productDtos = products.stream().map(this::buildProductDto).toList();
        return new PageImpl<>(productDtos, pageRequest, products.getTotalElements());
    }

    private ProductDto buildProductDto(Product product) {
        ProductDto productDto = productMapper.toDto(product);
        String staticUrl = imageUrlPrefix + productDto.getImageUrl();
        productDto.setImageUrl(staticUrl);
        return productDto;
    }

    @Override
    public List<ProductDto> searchProductByName(String keyword) {
        List<Product> products = productRepository.searchProductByName(keyword);
        return products.stream()
                .map(product -> {
                    ProductDto productDto = ProductMapper.INSTANCE.toDto(product);
                    String imageUrl = imageUrlPrefix + product.getImageUrl();
                    productDto.setImageUrl(imageUrl);
                    return productDto;
                })
                .toList();
    }


    @Override
    public void decreaseCount(Long productId, int quantity) {
        Product product = productFinderService.findProductById(productId);
        int currentQuantity = product.getQuantity();
        if (currentQuantity < quantity) {
            throw new InsufficientQuantityException("Insufficient quantity");
        }
        int newQuantity = currentQuantity - quantity;
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }



    @NotNull ProductDto getProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDiscountPrice(product.getDiscountPrice());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setQuantity(product.getQuantity());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());
        productDto.setDeleted(product.isDeleted());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setCategoryName(product.getCategory().getName());
        return productDto;
    }



    public List<ProductDto> findProductsByOrderId(Long orderId) {
        List<Product> products = productRepository.findProductsByOrderId(orderId);
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

//    @Override
@Override
public ProductSalesResponseDto countSoldProductsByYear(int year) {
    LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59, 59);

    List<ProductSalesProjectionByYear> results = productRepository.countSoldProductsByYear(startDate, endDate);
    List<ProductSoldCountDTO> productSoldCounts = new ArrayList<>();
    for (ProductSalesProjectionByYear projection : results) {
        String productName = projection.getProductName(); // Получаем название продукта
        int totalSold = projection.getTotalSold(); // Получаем общее количество проданных товаров
        ProductSoldCountDTO productSoldCount = new ProductSoldCountDTO(productName, totalSold);
        productSoldCounts.add(productSoldCount);
    }
    return new ProductSalesResponseDto(productSoldCounts);
}


//    @Override
//    public YearlySalesResponseDto getProductSalesStatistics() {
//        List<Object[]> results = productRepository.getProductSalesStatistics();
//        List<YearlyProductSalesDTO> yearlySales = new ArrayList<>();
//
//        for (Object[] row : results) {
//            Integer yearValue = ((Number) row[0]).intValue(); // Преобразуем год в Integer
//            Integer totalSold = ((Number) row[1]).intValue(); // Преобразуем общее количество продаж в Integer
//
//            YearlyProductSalesDTO yearlyProductSalesDTO = new YearlyProductSalesDTO(yearValue, totalSold);
//            yearlySales.add(yearlyProductSalesDTO);
//        }
//
//        return new YearlySalesResponseDto(yearlySales);
//    }

    @Override
    public YearlySalesResponseDto getProductSalesStatistics() {
        List<ProductSalesProjection> results = productRepository.getProductSalesStatistics();
        List<YearlyProductSalesDTO> yearlySales = new ArrayList<>();
        for (ProductSalesProjection projection : results) {
            Integer yearValue = projection.getSalesYear(); // Получаем год
            Integer totalSold = projection.getTotalSold(); // Получаем общее количество продаж
            YearlyProductSalesDTO yearlyProductSalesDTO = new YearlyProductSalesDTO(yearValue, totalSold);
            yearlySales.add(yearlyProductSalesDTO);
        }
        return new YearlySalesResponseDto(yearlySales);
    }


    //
    @Override
    public YearlySalesRevenueResponseDTO getTotalProductSalesRevenueByYear() {
        List<SalesRevenueProjection> results = productRepository.getSoldProductSalesStatistics();
        List<YearlySalesRevenueDTO> salesByYear = new ArrayList<>();
        for (SalesRevenueProjection projection : results) {
            Integer yearValue = projection.getYear(); // Получаем год
            BigDecimal totalRevenue = projection.getTotalRevenue(); // Получаем общую выручку
            YearlySalesRevenueDTO yearlySalesRevenueDTO = new YearlySalesRevenueDTO(yearValue, totalRevenue);
            salesByYear.add(yearlySalesRevenueDTO);
        }
        YearlySalesRevenueResponseDTO responseDTO = new YearlySalesRevenueResponseDTO();
        responseDTO.setYearlySales(salesByYear);
        return responseDTO;
    }


    @Override
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void applyNewYearDiscount() {
        List<Product> products = productRepository.findProductsByCategoryId(categoryId);
        for (Product product : products) {
            if (checkIfDiscountCanBeApplied(product)) {
                applyDiscount(product);
                productRepository.save(product);
            }
        }
    }

    // Удаление новогодней скидки через 5 минут после запуска
    @Override
    @Scheduled(cron = "0 0 0 8 1 ?")
    public void removeNewYearDiscount() {
        List<Product> products = productRepository.findProductsByCategoryId(categoryId);
        for (Product product : products) {
            removeDiscount(product);
            productRepository.save(product);
        }
    }

    @Override
    public Integer getProductRating(Long productId) {
        Product product = productFinderService.findProductById(productId);
        List<Rating> ratings = product.getRatings();


        // Вычисляем средний рейтинг
        double averageRating = ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0); // или другое значение по умолчанию, если не удалось вычислить

        // Округляем до целого числа и возвращаем
        return (int) Math.round(averageRating);
    }

    void applyDiscount(Product product) {
        BigDecimal currentPrice = product.getPrice();
        BigDecimal discount = currentPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal discountedPrice = currentPrice.subtract(discount);
        product.setDiscount(discountPercentage);
        product.setDiscountPrice(discountedPrice);
    }

    void removeDiscount(Product product) {
        product.setDiscount(null);
        product.setDiscountPrice(null);
    }

    boolean checkIfDiscountCanBeApplied(Product product) {
        return product.getQuantity() > 10;
    }


}




