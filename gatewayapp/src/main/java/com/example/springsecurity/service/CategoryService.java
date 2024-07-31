package com.example.springsecurity.service;


import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.CategoryDtoForClient;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.req.CategoryReq;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryDto> getAllCategories();

    CategoryDtoForClient getCategoryById(Long categoryId);

    void createCategory(CategoryReq categoryReq);

    public void updateCategory(Long categoryId, CategoryDto updatedCategoryDTO);

    void deleteCategory(Long categoryId);

//    CategoryDto getCategoryByIdWithProducts(Long categoryId);

}
