package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.req.CategoryReq;
import com.example.springsecurity.service.CategoryService;
import com.example.springsecurity.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryFinderService categoryFinderService;


    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    void testGetAllCategories() {
        List<Category> categories = new ArrayList<>();
        Category category1 = new Category();
        category1.setCategoryId(1L);
        category1.setName("Category 1");
        category1.setDescription("Test1");

        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setName("Category 2");
        category2.setDescription("Test2");


        categories.add(category1);
        categories.add(category2);

        when(categoryRepository.findAll()).thenReturn(categories);



        List<CategoryDto> categoryDtos = categoryService.getAllCategories();

        // Проверка корректности возвращаемых данных
        assertEquals(categories.size(), categoryDtos.size());
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            CategoryDto categoryDto = categoryDtos.get(i);
            assertEquals(category.getCategoryId(), categoryDto.getCategoryId());
            assertEquals(category.getName(), categoryDto.getName());
            assertEquals(category.getDescription(), categoryDto.getDescription());

        }
    }


    @Test
    void testCreateCategory() {

        CategoryReq categoryReq = new CategoryReq();
        categoryReq.setName("Test Category");
        categoryReq.setDescription("Test Description");

        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());

        categoryService.createCategory(categoryReq);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {

        Long categoryId = 1L;
        CategoryDto updatedCategoryDTO = new CategoryDto();
        updatedCategoryDTO.setName("Updated Category");
        updatedCategoryDTO.setDescription("Updated Description");


        Category existingCategory = new Category();
        when(categoryFinderService.findCategoryById(categoryId)).thenReturn(existingCategory);



        categoryService.updateCategory(categoryId, updatedCategoryDTO);


        assertEquals(updatedCategoryDTO.getName(), existingCategory.getName());
        assertEquals(updatedCategoryDTO.getDescription(), existingCategory.getDescription());


        verify(categoryRepository, times(1)).save(existingCategory);
    }



    @Test
    void testDeleteCategory() {

        Long categoryId = 1L;
        Category category = new Category();
        category.setDeleted(false);


        when(categoryFinderService.findCategoryById(categoryId)).thenReturn(category);


        categoryService.deleteCategory(categoryId);


        assertTrue(category.isDeleted());
        verify(categoryRepository, times(1)).save(category);
    }






}