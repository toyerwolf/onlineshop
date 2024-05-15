package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    @Spy
    private ProductInventoryService productInventoryService;



    @Test
    void testValidateProductQuantities_SufficientQuantity() {
        // Arrange
        Map<Product, Integer> productQuantities = new HashMap<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setQuantity(20);


        Product product2 = new Product();
        product2.setId(2L); // Set the ID for the product
        product2.setName("Product 2");
        product2.setQuantity(20);

        productQuantities.put(product1, 5);
        productQuantities.put(product2, 10);


        assertDoesNotThrow(() -> productInventoryService.validateProductQuantities(productQuantities));
    }


    @Test
    void testValidateProductQuantities_InsufficientQuantity() {
        // Arrange
        Map<Product, Integer> productQuantities = new HashMap<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setQuantity(2);
        productQuantities.put(product1, 3);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setQuantity(5);
        productQuantities.put(product2, 6);

        // Act and Assert
        assertThrows(InsufficientQuantityException.class, () -> productInventoryService.validateProductQuantities(productQuantities));
    }

    @Test
    void testGetProductQuantities() {
        // Arrange
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2);
        productQuantities.put(2L, 1);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        // Stubbing productService
        when(productService.findProductById(1L)).thenReturn(product1);
        when(productService.findProductById(2L)).thenReturn(product2);

        // Act
        Map<Product, Integer> result = productInventoryService.getProductQuantities(productQuantities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.containsKey(product1));
        assertTrue(result.containsKey(product2));

        assertEquals(2, result.get(product1).intValue());
        assertEquals(1, result.get(product2).intValue());

        // Verify that productService.findProductById is called for each product ID
        verify(productService).findProductById(1L);
        verify(productService).findProductById(2L);
    }

    @Test
    void testDecreaseProductCount() {
        // Arrange
        Map<Product, Integer> productQuantities = new HashMap<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setQuantity(10);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setQuantity(10);

        productQuantities.put(product1, 2);
        productQuantities.put(product2, 1);

        // Act
        productInventoryService.decreaseProductCount(productQuantities);

        // Assert
        verify(productService).decreaseCount(1L, 2);
        verify(productService).decreaseCount(2L, 1);
    }
    @Test
    void testIncreaseProductCount() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setQuantity(5);

        int quantityToAdd = 3;

        when(productRepository.findById(existingProduct.getId())).thenReturn(Optional.of(existingProduct));

        // Act
        productInventoryService.increaseProductCount(existingProduct, quantityToAdd);

        // Assert
        assertEquals(existingProduct.getQuantity(), 8); // увеличено на 3
        verify(productRepository).save(existingProduct);
    }
}

