package com.example.springsecurity.controller;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.dto.CategoryDtoForClient;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.ProductDtoContainer;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.req.CategoryReq;
import com.example.springsecurity.service.CategoryService;
import com.example.springsecurity.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDtoForClient> getCategoryById(@PathVariable Long categoryId) {
        CategoryDtoForClient categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }



    @Secured("ADMIN")
    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryReq categoryReq) {
        categoryService.createCategory(categoryReq);
        String message = "Category created successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    @Secured("ADMIN")
    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto updatedCategoryDTO) {
        categoryService.updateCategory(categoryId, updatedCategoryDTO);
        return ResponseEntity.ok("Updated successfully");
    }


    @Secured("ADMIN")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Secured({"USER"})
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<ProductDtoContainer> getProductsByCategoryId(@PathVariable Long categoryId) {
        ProductDtoContainer container = productService.findProductsByCategoryId(categoryId);
        return ResponseEntity.ok(container);
    }

}
