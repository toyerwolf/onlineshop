package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Category;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.req.ProductRequest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T14:50:41+0400",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setCategoryName( productCategoryName( product ) );
        productDto.setId( product.getId() );
        productDto.setName( product.getName() );
        productDto.setDescription( product.getDescription() );
        productDto.setPrice( product.getPrice() );
        productDto.setDiscountPrice( product.getDiscountPrice() );
        productDto.setQuantity( product.getQuantity() );
        productDto.setCreatedAt( product.getCreatedAt() );
        productDto.setUpdatedAt( product.getUpdatedAt() );
        productDto.setDeleted( product.isDeleted() );
        productDto.setImageUrl( product.getImageUrl() );

        return productDto;
    }

    @Override
    public List<ProductDto> toDtoList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDto> list = new ArrayList<ProductDto>( products.size() );
        for ( Product product : products ) {
            list.add( toDto( product ) );
        }

        return list;
    }

    @Override
    public void updateProductFromRequest(ProductRequest productRequest, Product product) {
        if ( productRequest == null ) {
            return;
        }

        product.setName( productRequest.getName() );
        product.setDescription( productRequest.getDescription() );
        product.setPrice( productRequest.getPrice() );
        product.setDiscountPrice( productRequest.getDiscountPrice() );
        product.setDiscount( productRequest.getDiscount() );
        product.setQuantity( productRequest.getQuantity() );
        product.setCreatedAt( productRequest.getCreatedAt() );
        product.setUpdatedAt( productRequest.getUpdatedAt() );
        product.setDeleted( productRequest.isDeleted() );
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
