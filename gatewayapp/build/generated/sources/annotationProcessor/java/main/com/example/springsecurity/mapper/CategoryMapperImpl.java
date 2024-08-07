package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.CategoryDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.req.CategoryReq;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T14:50:41+0400",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto categoryToCategoryDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setCategoryId( category.getCategoryId() );
        categoryDto.setName( category.getName() );
        categoryDto.setDescription( category.getDescription() );
        categoryDto.setDeleted( category.isDeleted() );

        return categoryDto;
    }

    @Override
    public Category categoryDTOToCategory(CategoryReq categoryReq) {
        if ( categoryReq == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( categoryReq.getName() );
        category.setDescription( categoryReq.getDescription() );
        category.setDeleted( categoryReq.isDeleted() );

        return category;
    }

    @Override
    public List<CategoryDto> categoriesToCategoryDTOs(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryDto> list = new ArrayList<CategoryDto>( categories.size() );
        for ( Category category : categories ) {
            list.add( categoryToCategoryDTO( category ) );
        }

        return list;
    }
}
