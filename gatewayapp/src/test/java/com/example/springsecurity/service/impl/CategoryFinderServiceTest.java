package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Category;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryFinderServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryFinderService categoryFinderService;



    @Test
    void testFindCategoryById_Success() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName("Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryFinderService.findCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("Test Category", result.getName());
    }

    @Test
    void testFindCategoryById_NotFound() {
        // Arrange
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                        categoryFinderService.findCategoryById(categoryId),
                "Expected findCategoryById() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Category not found"));
    }
}

