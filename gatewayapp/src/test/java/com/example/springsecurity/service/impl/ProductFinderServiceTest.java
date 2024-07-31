package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductFinderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductFinderService productFinderService;


    @Test
    public void testFindProductById_ProductFound() {
        // Mock data
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        // Mock repository behavior
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Call the method
        Product foundProduct = productFinderService.findProductById(productId);

        // Verify the result
        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());

        // Verify interactions
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testFindProductById_ProductNotFound() {
        // Mock data
        Long productId = 1L;

        // Mock repository behavior
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Call the method and assert exception
        NotFoundException thrownException = assertThrows(NotFoundException.class, () -> {
            productFinderService.findProductById(productId);
        });

        assertEquals("Product not found", thrownException.getMessage());

        // Verify interactions
        verify(productRepository, times(1)).findById(productId);
    }
}