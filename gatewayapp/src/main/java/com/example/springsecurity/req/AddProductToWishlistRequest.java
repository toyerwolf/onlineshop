package com.example.springsecurity.req;


import lombok.Data;

@Data
public class AddProductToWishlistRequest {
    private Long customerId;
    private Long productId;
}
