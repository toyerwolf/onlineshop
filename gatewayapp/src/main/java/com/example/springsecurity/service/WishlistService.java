package com.example.springsecurity.service;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.req.AddProductToWishlistRequest;

import java.util.List;

public interface WishlistService {

    void addProductToWishlist(AddProductToWishlistRequest request);

    List<ProductDto> getAllProductsInWishlist(Long customerId);

     void removeProductFromWishlist(Long customerId, Long productId);
}
