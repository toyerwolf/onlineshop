package com.example.springsecurity.controller;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.CategoryDtoForClient;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.req.CategoryReq;
import com.example.springsecurity.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDtoForClient> getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryReq categoryReq) {
        categoryService.createCategory(categoryReq);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto updatedCategoryDTO) {
        categoryService.updateCategory(categoryId, updatedCategoryDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/with-products")
    public CategoryDto getCategoryByIdWithProducts(@PathVariable Long categoryId) {
        return categoryService.getCategoryByIdWithProducts(categoryId);
    }

}
