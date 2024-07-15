package com.example.springsecurity.controller;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.req.AddProductToWishlistRequest;
import com.example.springsecurity.service.WishlistService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@AllArgsConstructor
public class WishlistController {

    private WishlistService wishlistService;


    @Secured("USER")
    @PostMapping("/addProduct")
    public ResponseEntity<String> addProductToWishlist(@RequestBody AddProductToWishlistRequest request) {
        wishlistService.addProductToWishlist(request);
        return ResponseEntity.ok("Product added to wishlist successfully");
    }

    @Secured("USER")
    @GetMapping("/getAllProducts/{customerId}")
    public ResponseEntity<List<ProductDto>> getAllProductsInWishlist(@PathVariable Long customerId) {
        List<ProductDto> products = wishlistService.getAllProductsInWishlist(customerId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/removeProduct")
    @Secured("USER")
    public ResponseEntity<String> removeProductFromWishlist(@RequestParam Long customerId, @RequestParam Long productId) {
        wishlistService.removeProductFromWishlist(customerId, productId);
        return ResponseEntity.ok("Product removed from wishlist successfully");
    }


}