package com.example.springsecurity.controller;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.req.ProductRequest;
import com.example.springsecurity.service.ImageService;
import com.example.springsecurity.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;




    @Secured("ADMIN")
    @PostMapping("/{categoryId}")
    public ResponseEntity<String> addProductToCategory(@ModelAttribute ProductRequest productRequest,
                                                       @PathVariable Long categoryId,
                                                       @RequestParam("image")MultipartFile image) throws IOException {
        productService.addProductToCategory(productRequest, categoryId,image);
        return ResponseEntity.ok("Product added to category successfully");
    }

//    @GetMapping("/category/{categoryId}")
//    public List<ProductDto> findProductsByCategoryId(@PathVariable Long categoryId) {
//        return productService.findProductsByCategoryId(categoryId);
//    }

    @Secured("ADMIN")
    @PatchMapping("/{productId}/images")
    public ResponseEntity<?> changeProductImages(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
        imageService.changeImage(productId, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }


    @Secured("ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        productService.updateProduct(id, productRequest);
        return ResponseEntity.ok("Updated success");
    }


    @Secured("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam int page,
                                                           @RequestParam int pageSize) {
        Page<ProductDto> products = productService.getAllProducts(page,pageSize);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/search")
    public List<ProductDto> searchProductByName(@RequestParam String keyword) {
        return productService.searchProductByName(keyword);
}

//    @Secured("ADMIN")
//    @PatchMapping("/{productId}/decrease-count")
//    public ResponseEntity<String> decreaseCount(@PathVariable Long productId,@RequestParam int quantity){
//        productService.decreaseCount(productId, quantity);
//        return ResponseEntity.ok("Product count decreased successfully");
//    }

//    @GetMapping("/{orderId}/products")
//    public ResponseEntity<List<ProductDto>> getProductsByOrderId(@PathVariable Long orderId) {
//        List<ProductDto> products = productService.findProductsByOrderId(orderId);
//        return ResponseEntity.ok(products);
//    }
}
