package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Category;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CategoryRepository;
import com.example.springsecurity.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryFinderService {


    private final CategoryRepository categoryRepository;

    Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

}
