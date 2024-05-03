package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.req.CategoryReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);



    CategoryDto categoryToCategoryDTO(Category category);

    @Mapping(target = "categoryId", ignore = true)
    Category categoryDTOToCategory(CategoryReq categoryReq);


    List<CategoryDto> categoriesToCategoryDTOs(List<Category> categories);
}
