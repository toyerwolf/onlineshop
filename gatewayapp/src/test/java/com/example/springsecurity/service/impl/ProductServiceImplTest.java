package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Rating;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.projection.ProductSalesProjection;
import com.example.springsecurity.projection.ProductSalesProjectionByYear;
import com.example.springsecurity.projection.SalesRevenueProjection;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.ProductRequest;
import com.example.springsecurity.service.ImageService;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    private final Long categoryId = 1L;
    private final BigDecimal discountPercentage = BigDecimal.valueOf(20);

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductFinderService productFinderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageService imageService;
    @InjectMocks
    @Spy
    private ProductServiceImpl productService;

    @Mock
    private CategoryFinderService categoryFinderService;


    private static final String IMAGE_URL_PREFIX = "/static/";



    //sozdayem category potom produktrequest,i image
    // esli category i image naydeni vizivayem metod addProduct
    //expected dva potomu chto pri sozdanii new category  est kakoy to produkt
    @Test
    void testAddProductToCategory_Success() throws IOException {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(BigDecimal.valueOf(10.0));
        productRequest.setQuantity(100);
        productRequest.setDiscountPrice(BigDecimal.valueOf(5.0));
        productRequest.setDiscount(BigDecimal.valueOf(50));

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{});
        String imageName = "test.jpg";

        when(categoryFinderService.findCategoryById(categoryId)).thenReturn(category);
        when(imageService.saveImage(image)).thenReturn(imageName);

        // Act
        productService.addProductToCategory(productRequest, categoryId, image);

        // Assert
        verify(productRepository, times(1)).save(any(Product.class));
        verify(categoryRepository, times(1)).save(any(Category.class));
        assertEquals(2, category.getProducts().size());
        Product savedProduct = category.getProducts().iterator().next();
        assertEquals(productRequest.getName(), savedProduct.getName());
        assertEquals(productRequest.getDescription(), savedProduct.getDescription());
        assertEquals(productRequest.getPrice(), savedProduct.getPrice());
        assertEquals(productRequest.getQuantity(), savedProduct.getQuantity());
        assertEquals(productRequest.getDiscountPrice(), savedProduct.getDiscountPrice());
        assertEquals(productRequest.getDiscount(), savedProduct.getDiscount());
        assertEquals(imageName, savedProduct.getImageUrl());
        assertFalse(savedProduct.isDeleted());
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
    }

    @Test
    void testAddProductToCategory_ProductAlreadyExists() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Existing Product");
        Long categoryId = 1L;
        MultipartFile image = mock(MultipartFile.class);

        // Mocking existing product
        when(productRepository.findByName("Existing Product")).thenReturn(Optional.of(new Product()));

        // Act & Assert
        assertThrows(AlreadyExistException.class, () -> {
            productService.addProductToCategory(productRequest, categoryId, image);
        });
    }





    @Test
    void testFindProductsByCategoryId_Success() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        // Set image URL prefix
        productService.setImageUrlPrefix(IMAGE_URL_PREFIX);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setImageUrl("product1.jpg");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setImageUrl("product2.jpg");
        product2.setCategory(category);

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        when(productRepository.findProductsByCategoryId(categoryId)).thenReturn(products);

        // Act
        ProductDtoContainer result = productService.findProductsByCategoryId(categoryId);

        // Assert
        assertEquals(2, result.getProducts().size());
        ProductDto dto1 = result.getProducts().get(0);
        assertEquals("Product 1", dto1.getName());
        assertNotNull(dto1.getImageUrl());
        assertEquals( "product1.jpg", dto1.getImageUrl()); // Use the correct prefix
        ProductDto dto2 = result.getProducts().get(1);
        assertEquals("Product 2", dto2.getName());
        assertNotNull(dto2.getImageUrl());
        assertEquals("product2.jpg", dto2.getImageUrl()); // Use the correct prefix
    }




    //sozdayem dva produkta dobavlayem v list,
    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> sampleProducts = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setImageUrl("product1.jpg");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setImageUrl("product2.jpg");

        sampleProducts.add(product1);
        sampleProducts.add(product2);

        // Mocking repository response
        Pageable pageRequest = PageRequest.of(0, 1);
        Page<Product> samplePage = new PageImpl<>(sampleProducts, pageRequest, sampleProducts.size());
        when(productRepository.findAll(pageRequest)).thenReturn(samplePage);


        Page<ProductDto> result = productService.getAllProducts(1, 1);

        // Verifying the result
        assertEquals(sampleProducts.size(), result.getContent().size());
        assertEquals(sampleProducts.size(), result.getTotalElements());
        assertEquals(0, result.getNumber()); // Page number starts from 0, so 1st page is 0,because in method pageNumber-1
        assertEquals(1, result.getSize()); // Page size should be 1
    }

    @Test
    @Transactional
    void testUpdateProduct_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        // Создаем запрос на обновление продукта,можна написать и другие поля
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product Name");

        // Мокируем вызовы репозитория
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Обновляем продукт
        productService.updateProduct(productId, productRequest);

        // Проверяем, что метод save был вызван один раз
        verify(productRepository, times(1)).save(product);

        // Проверяем, что название продукта обновлено
        assertEquals(productRequest.getName(), product.getName());
    }


    @Test
    @Transactional
    void testDeleteProduct_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Проверяем, что метод удаления продукта не выбрасывает исключение
        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        // Проверяем, что флаг deleted установлен в true
        verify(productRepository, times(1)).save(product);
        assertTrue(product.isDeleted());
    }


    @Test
    void testSearchProductByName() {
        // Arrange
        Product product1 = createProduct(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.00), 5);
        Product product2 = createProduct(2L, "Product 2", "Description 2", BigDecimal.valueOf(15.00), 3);

        // Указываем ожидаемый результат поиска
        List<Product> expectedProducts = Arrays.asList(product1, product2);

        // Мокируем вызовы репозитория
        when(productRepository.searchProductByName("Product")).thenReturn(expectedProducts);

        // Вызываем метод для тестирования
        List<ProductDto> result = productService.searchProductByName("Product");
        assertNotNull(result);
        // Проверяем, что результат соответствует ожидаемому
        assertEquals(expectedProducts.size(), result.size());
        expectedProducts.forEach((expectedProduct) -> {
            // Находим соответствующий продукт в результирующем списке по id
            ProductDto actualProductDto = result.stream()
                    .filter(dto -> dto.getId().equals(expectedProduct.getId()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(actualProductDto);
            assertEquals(expectedProduct.getName(), actualProductDto.getName());
            assertEquals(expectedProduct.getName(), actualProductDto.getName());

        });
    }

    @Test
    void testSearchProductByName_EmptyResult() {
        // Mock empty list
        when(productRepository.searchProductByName("NonExistingProduct")).thenReturn(Collections.emptyList());
        List<ProductDto> result = productService.searchProductByName("NonExistingProduct");
        assertEquals(0, result.size());
    }

    @Test
    void testDecreaseCount_Success() {
        // Arrange
        Long productId = 1L;
        int currentQuantity = 10;
        int quantityToDecrease = 5;
        Product product = new Product();
        product.setId(productId);
        product.setQuantity(currentQuantity);

        // Mocking repository response
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Act
        assertDoesNotThrow(() -> productService.decreaseCount(productId, quantityToDecrease));

        // Assert
        assertEquals(currentQuantity - quantityToDecrease, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDecreaseCount_InsufficientQuantity() {
        // Arrange
        Long productId = 1L;
        int currentQuantity = 3;
        int quantityToDecrease = 5;
        Product product = new Product();
        product.setId(productId);
        product.setQuantity(currentQuantity);

        // Mocking repository
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Act and Assert
        InsufficientQuantityException exception = assertThrows(InsufficientQuantityException.class, () ->
                productService.decreaseCount(productId, quantityToDecrease));
        assertEquals("Insufficient quantity", exception.getMessage());

        // Verify that save method is not invoked
        verify(productRepository, never()).save(any());
    }



    @Test
    void testGetProductDto() {
        // Arrange
        Product product = new Product();

        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("Test Category");
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description of test product");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setQuantity(100);
        product.setCreatedAt(LocalDateTime.now().minusDays(1));
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);
        product.setImageUrl("test_product.jpg");
        product.setCategory(category);

        // Act
        ProductDto productDto = productService.getProductDto(product);

        // Assert
        assertNotNull(productDto);
        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(product.getDescription(), productDto.getDescription());
        assertEquals(product.getPrice(), productDto.getPrice());
        assertEquals(product.getQuantity(), productDto.getQuantity());
        assertEquals(product.getCreatedAt(), productDto.getCreatedAt());
        assertEquals(product.getUpdatedAt(), productDto.getUpdatedAt());
        assertEquals(product.isDeleted(), productDto.isDeleted());
        assertEquals(product.getImageUrl(), productDto.getImageUrl());
        assertEquals(category.getName(), productDto.getCategoryName());
    }

    @Test
    void testFindProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product expectedProduct = new Product();
        expectedProduct.setId(productId);

        when(productFinderService.findProductById(productId)).thenReturn(expectedProduct);

        // Act
        Product actualProduct = productFinderService.findProductById(productId);

        // Assert
        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getId(), actualProduct.getId());
    }
    @Test
    void testFindProductsByOrderId_Success() {
        // Arrange
        Long orderId = 1L;
        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(createProduct(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.0), 5));
        expectedProducts.add(createProduct(2L, "Product 2", "Description 2", BigDecimal.valueOf(20.0), 10));

        when(productRepository.findProductsByOrderId(orderId)).thenReturn(expectedProducts);

        // Act
        List<ProductDto> actualProductDtos = productService.findProductsByOrderId(orderId);

        // Assert
        assertEquals(expectedProducts.size(), actualProductDtos.size());

        // Проверка каждого поля ProductDto
        for (int i = 0; i < expectedProducts.size(); i++) {
            Product expectedProduct = expectedProducts.get(i);
            ProductDto actualProductDto = actualProductDtos.get(i);
            assertEquals(expectedProduct.getId(), actualProductDto.getId());
            assertEquals(expectedProduct.getName(), actualProductDto.getName());
            assertEquals(expectedProduct.getDescription(), actualProductDto.getDescription());
            assertEquals(expectedProduct.getPrice(), actualProductDto.getPrice());
            assertEquals(expectedProduct.getQuantity(), actualProductDto.getQuantity());
            assertEquals(expectedProduct.getCreatedAt(), actualProductDto.getCreatedAt());
            assertEquals(expectedProduct.getUpdatedAt(), actualProductDto.getUpdatedAt());
            assertEquals(expectedProduct.isDeleted(), actualProductDto.isDeleted());
            assertEquals(expectedProduct.getImageUrl(), actualProductDto.getImageUrl());
        }
    }

    // Вспомогательный метод для создания объекта Product
    private Product createProduct(Long id, String name, String description, BigDecimal price, int quantity) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);
        product.setImageUrl("image-url");
        return product;
    }

    @Test
    void testCountSoldProductsByYear() {
        // Arrange
        int year = 2023;
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        // Создаем фиктивные объекты проекции
        ProductSalesProjectionByYear projection1 = mock(ProductSalesProjectionByYear.class);
        when(projection1.getProductName()).thenReturn("Product 1");
        when(projection1.getTotalSold()).thenReturn(50);

        ProductSalesProjectionByYear projection2 = mock(ProductSalesProjectionByYear.class);
        when(projection2.getProductName()).thenReturn("Product 2");
        when(projection2.getTotalSold()).thenReturn(25);

        List<ProductSalesProjectionByYear> mockResults = new ArrayList<>();
        mockResults.add(projection1);
        mockResults.add(projection2);

        // Мокаем репозиторий
        when(productRepository.countSoldProductsByYear(startOfYear, endOfYear)).thenReturn(mockResults);

        // Act
        ProductSalesResponseDto productSalesResponseDto = productService.countSoldProductsByYear(year);

        // Assert
        assertNotNull(productSalesResponseDto);
        List<ProductSoldCountDTO> soldProductsByYear = productSalesResponseDto.getProductSoldCounts();
        assertNotNull(soldProductsByYear);
        assertEquals(2, soldProductsByYear.size());

        Map<String, Integer> expectedSales = new HashMap<>();
        expectedSales.put("Product 1", 50);
        expectedSales.put("Product 2", 25);

        for (ProductSoldCountDTO productSoldCountDTO : soldProductsByYear) {
            String productName = productSoldCountDTO.getProductName();
            Integer totalSold = productSoldCountDTO.getSoldCount();
            assertNotNull(productName);
            assertNotNull(totalSold);

            if (expectedSales.containsKey(productName)) {
                assertEquals(expectedSales.get(productName).intValue(), totalSold.intValue());
            }
        }
    }


    @Test
    public void testGetProductSalesStatistics() {
        // Arrange
        // Создаем фиктивные объекты проекции
        ProductSalesProjection projection1 = mock(ProductSalesProjection.class);
        when(projection1.getSalesYear()).thenReturn(2023);
        when(projection1.getTotalSold()).thenReturn(10);

        ProductSalesProjection projection2 = mock(ProductSalesProjection.class);
        when(projection2.getSalesYear()).thenReturn(2024);
        when(projection2.getTotalSold()).thenReturn(20);

        List<ProductSalesProjection> mockResults = new ArrayList<>();
        mockResults.add(projection1);
        mockResults.add(projection2);

        // Мокаем репозиторий
        when(productRepository.getProductSalesStatistics()).thenReturn(mockResults);

        // Act
        YearlySalesResponseDto salesResponseDto = productService.getProductSalesStatistics();

        // Assert
        List<YearlyProductSalesDTO> yearlySales = salesResponseDto.getYearlySales();
        assertEquals(2, yearlySales.size());

        assertEquals(2023, yearlySales.get(0).getYear());
        assertEquals(10, yearlySales.get(0).getTotalSold());

        assertEquals(2024, yearlySales.get(1).getYear());
        assertEquals(20, yearlySales.get(1).getTotalSold());
    }




    @Test
    public void testGetTotalProductSalesRevenueByYear() {
        // Arrange
        // Создаем фиктивные объекты проекции
        SalesRevenueProjection projection1 = mock(SalesRevenueProjection.class);
        when(projection1.getYear()).thenReturn(2023);
        when(projection1.getTotalRevenue()).thenReturn(BigDecimal.valueOf(10000));

        SalesRevenueProjection projection2 = mock(SalesRevenueProjection.class);
        when(projection2.getYear()).thenReturn(2024);
        when(projection2.getTotalRevenue()).thenReturn(BigDecimal.valueOf(20000));

        List<SalesRevenueProjection> mockResults = new ArrayList<>();
        mockResults.add(projection1);
        mockResults.add(projection2);

        // Мокаем репозиторий
        when(productRepository.getSoldProductSalesStatistics()).thenReturn(mockResults);

        // Act
        YearlySalesRevenueResponseDTO salesResponseDto = productService.getTotalProductSalesRevenueByYear();

        // Assert
        List<YearlySalesRevenueDTO> yearlySales = salesResponseDto.getYearlySales();
        assertEquals(2, yearlySales.size());

        assertEquals(2023, yearlySales.get(0).getYear());
        assertEquals(BigDecimal.valueOf(10000), yearlySales.get(0).getTotalRevenue());

        assertEquals(2024, yearlySales.get(1).getYear());
        assertEquals(BigDecimal.valueOf(20000), yearlySales.get(1).getTotalRevenue());
    }


    @Test
    void testApplyNewYearDiscount() {
        // Arrange
        long categoryId = 1L;
        List<Product> products = new ArrayList<>();
        products.add(createProductWithQuantityAndPrice(1L, 20, BigDecimal.valueOf(100)));
        products.add(createProductWithQuantityAndPrice(2L, 5, BigDecimal.valueOf(200)));
        when(productRepository.findProductsByCategoryId(categoryId)).thenReturn(products);

        // Act
        productService.applyNewYearDiscount();

        // Assert
        verify(productRepository, times(1)).findProductsByCategoryId(categoryId);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productRepository, times(1)).save(products.get(0));
        verify(productRepository, times(0)).save(products.get(1));// Проверяем, что товар с количеством менее или равным 10 не сохраняется
    }

    @Test
    void testRemoveNewYearDiscount() {
        // Arrange
        long categoryId = 1L;
        List<Product> products = new ArrayList<>();
        // Создаем два продукта, один с скидкой, другой без
        Product productWithDiscount = createProductWithDiscount(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(90));
        Product productWithoutDiscount = createProductWithDiscount(2L, BigDecimal.valueOf(200), null);
        products.add(productWithDiscount);
        products.add(productWithoutDiscount);
        // Задаем поведение мокитованным методам
        when(productRepository.findProductsByCategoryId(categoryId)).thenReturn(products);

        // Act
        productService.removeNewYearDiscount();

        // Assert
        // Проверяем, что метод removeDiscount вызывался для каждого продукта
        verify(productService, times(1)).removeDiscount(productWithDiscount);
        verify(productService, times(1)).removeDiscount(productWithoutDiscount);
        // Проверяем, что изменения были сохранены в репозитории
        verify(productRepository, times(2)).save(any(Product.class));
        // Проверяем, что скидка была удалена для каждого продукта
        assertNull(productWithDiscount.getDiscount());
        assertNull(productWithDiscount.getDiscountPrice());
        assertNull(productWithoutDiscount.getDiscount());
        assertNull(productWithoutDiscount.getDiscountPrice());
    }

    private Product createProductWithDiscount(Long id, BigDecimal price, BigDecimal discountPrice) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        product.setDiscountPrice(discountPrice);
        if (discountPrice != null) {
            BigDecimal currentPrice = price.subtract(discountPrice);
            BigDecimal discountPercentage = discountPrice.divide(price, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            product.setDiscount(discountPercentage);
        }
        return product;
    }


    @Test
    void testDiscountCanBeAppliedWhenQuantityIsGreaterThanTen() {
        Product product = new Product();
        product.setQuantity(15);
        boolean result = productService.checkIfDiscountCanBeApplied(product);
        assertTrue(result);
    }

    @Test
    void testDiscountCannotBeAppliedWhenQuantityIsTen() {
        Product product = new Product();
        product.setQuantity(10);
        boolean result = productService.checkIfDiscountCanBeApplied(product);
        assertFalse(result);
    }

    @Test
    void testDiscountCannotBeAppliedWhenQuantityIsLessThanTen() {
        Product product = new Product();
        product.setQuantity(8);
        boolean result = productService.checkIfDiscountCanBeApplied(product);
        assertFalse(result);
    }

    @Test
    void testApplyDiscount() {
        // Arrange
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));

        ProductServiceImpl productService = new ProductServiceImpl(null, null, null,null,null);
        productService.setDiscountPercentage(BigDecimal.valueOf(20));


        productService.applyDiscount(product);
        assertThat(product.getDiscount()).isEqualTo(BigDecimal.valueOf(20));

        BigDecimal expectedDiscountAmount = product.getPrice().multiply(BigDecimal.valueOf(20))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal expectedDiscountedPrice = product.getPrice().subtract(expectedDiscountAmount);

        assertThat(product.getDiscountPrice()).isEqualByComparingTo(expectedDiscountedPrice);
    }

    @Test
    void testRemoveDiscount() {
        // Arrange
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));
        product.setDiscount(new BigDecimal("20.00"));
        product.setDiscountPrice(new BigDecimal("80.00"));

        ProductServiceImpl productService = new ProductServiceImpl(null,null, null,null,null);

        // Act
        productService.removeDiscount(product);

        // Assert
        assertThat(product.getDiscount()).isNull();
        assertThat(product.getDiscountPrice()).isNull();
    }

    @Test
    public void testGetProductRating_ProductFoundWithRatings() {
        // Mock data
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(Rating.builder().rating(4).build());
        ratings.add(Rating.builder().rating(5).build());
        product.setRatings(ratings);

        // Mock productFinderService behavior
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Call the method
        Integer averageRating = productService.getProductRating(productId);

        // Verify the result
        assertNotNull(averageRating);
        assertEquals(5, averageRating); // Average of {4, 5} is 4.5, rounded to nearest integer is 5

        // Verify interactions
        verify(productFinderService, times(1)).findProductById(productId);
    }


    private Product createProductWithQuantityAndPrice(Long id, int quantity, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setQuantity(quantity);
        product.setPrice(price);
        return product;
    }
}
























