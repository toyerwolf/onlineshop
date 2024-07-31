package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Wishlist;
import com.example.springsecurity.entity.WishlistItem;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.WishListItemRepository;
import com.example.springsecurity.repository.WishListRepository;
import com.example.springsecurity.req.AddProductToWishlistRequest;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.ProductService;
import com.example.springsecurity.service.WishlistService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WishListServiceImpl implements WishlistService {

    private final CustomerService customerService;
    private ProductService productService;
    private WishListRepository wishlistRepository;;
    private WishListItemRepository wishListItemRepository;
    private CustomerRepository customerRepository;
    private final ProductFinderService productFinderService;
    private final CustomerFinderService customerFinderService;


    @Override
    public void addProductToWishlist(AddProductToWishlistRequest request) {
        // Получаем клиента по ID
        Customer customer = customerFinderService.findCustomerById(request.getCustomerId());

        // Получаем продукт по ID
        Product product = productFinderService.findProductById(request.getProductId());

        // Получаем или создаем wishlist клиента
        Wishlist wishlist = customer.getWishlist();
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setCustomer(customer);
            wishlist.setCreatedAt(LocalDateTime.now());
            wishlist = wishlistRepository.save(wishlist);
            customer.setWishlist(wishlist);
            customerRepository.save(customer);
        }

        // Создаем новый элемент wishlist для продукта
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setProduct(product);
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setCreatedAt(LocalDateTime.now()); // Устанавливаем время создания элемента

        wishListItemRepository.save(wishlistItem);
    }

    @Override
    public List<ProductDto> getAllProductsInWishlist(Long customerId) {
        Customer customer = customerFinderService.findCustomerById(customerId);
        Wishlist wishlist = customer.getWishlist();
        if (wishlist == null) {
            return Collections.emptyList(); // Возвращаем пустой список, если wishlist не существует
        }
        return wishlist.getWishlistItems().stream()
                .map(wishlistItem -> {
                    Product product = wishlistItem.getProduct();
                    ProductDto dto = new ProductDto();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setDiscountPrice(product.getDiscountPrice());
                    dto.setQuantity(product.getQuantity());
                    dto.setCreatedAt(product.getCreatedAt());
                    dto.setUpdatedAt(product.getUpdatedAt());
                    dto.setDeleted(product.isDeleted());

                    dto.setImageUrl("/static/" + product.getImageUrl());
                    dto.setCategoryName(product.getCategory().getName());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeProductFromWishlist(Long customerId, Long productId) {
        // Проверяем существование Wishlist у Customer
        Customer customer = customerFinderService.findCustomerById(customerId);
        if (customer == null || customer.getWishlist() == null) {
            throw new NotFoundException("Wishlist not found for customer with ID " + customerId);
        }

        // Удаляем продукт из Wishlist по customerId и productId
        Wishlist wishlist = customer.getWishlist();
        WishlistItem wishlistItemToRemove = wishlist.getWishlistItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (wishlistItemToRemove == null) {
            throw new NotFoundException("WishlistItem not found for customer with ID " + customerId + " and product with ID " + productId);
        }

        wishlist.getWishlistItems().remove(wishlistItemToRemove);
        wishListItemRepository.delete(wishlistItemToRemove);
    }
}

