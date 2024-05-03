package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.req.ProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE= Mappers.getMapper(ProductMapper.class);



    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);
    List<ProductDto> toDtoList(List<Product> products);



    @Mapping(target = "id", ignore = true)
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);
}
