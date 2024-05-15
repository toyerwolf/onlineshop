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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductService productService;


    @Override
    public List<CategoryDto> getAllCategories() {
        return CategoryMapper.INSTANCE.categoriesToCategoryDTOs(categoryRepository.findAll());
    }



    @Override
    public Optional<CategoryDtoForClient> getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            CategoryDtoForClient categoryDto = new CategoryDtoForClient();
            categoryDto.setCategoryId(category.getCategoryId());
            categoryDto.setName(category.getName());
            return Optional.of(categoryDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void createCategory(CategoryReq categoryReq) {
        Category category = CategoryMapper.INSTANCE.categoryDTOToCategory(categoryReq);
        categoryRepository.save(category);
    }
    @Override
    public void updateCategory(Long categoryId, CategoryDto updatedCategoryDTO) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(categoryId);
        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();
            existingCategory.setName(updatedCategoryDTO.getName());
            existingCategory.setDescription(updatedCategoryDTO.getDescription());
            categoryRepository.save(existingCategory);
        } else {
            throw new NotFoundException("Category with " + categoryId + " not found");
        }
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        categoryOptional.ifPresent(category -> {
            category.setDeleted(true);
            categoryRepository.save(category);
        });
    }

//    @Override
//    public CategoryDto getCategoryByIdWithProducts(Long categoryId) {
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new NotFoundException("Category not found for id: " + categoryId));
//        List<ProductDto> productsInCategory = productService.findProductsByCategoryId(categoryId);
//        CategoryDto categoryDto = CategoryMapper.INSTANCE.categoryToCategoryDTO(category);
//        categoryDto.setProductDtoList(productsInCategory);
//        return categoryDto;
//    }



    }


