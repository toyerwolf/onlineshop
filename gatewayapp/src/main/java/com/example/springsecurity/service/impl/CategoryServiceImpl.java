package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.CategoryDtoForClient;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.CategoryMapper;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.req.CategoryReq;
import com.example.springsecurity.service.CategoryService;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryFinderService categoryFinderService;


    @Override
    public List<CategoryDto> getAllCategories() {
        return CategoryMapper.INSTANCE.categoriesToCategoryDTOs(categoryRepository.findAll());
    }



    @Override
    public CategoryDtoForClient getCategoryById(Long categoryId) {
        Category category = categoryFinderService.findCategoryById(categoryId);
        return new CategoryDtoForClient(category.getCategoryId(), category.getName());
    }

    @Override
    public void createCategory(CategoryReq categoryReq) {
        Category category = CategoryMapper.INSTANCE.categoryDTOToCategory(categoryReq);
        categoryRepository.save(category);
    }
    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryDto updatedCategoryDTO) {
        Category existingCategory = categoryFinderService.findCategoryById(categoryId);
        existingCategory.setName(updatedCategoryDTO.getName());
        existingCategory.setDescription(updatedCategoryDTO.getDescription());
        categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryFinderService.findCategoryById(categoryId);
        category.setDeleted(true);
        categoryRepository.save(category);
    }




    }


