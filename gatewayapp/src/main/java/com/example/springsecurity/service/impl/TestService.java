//package com.example.springsecurity.service.impl;
//
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.entity.CustomerDiscount;
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.entity.Product;
//
//import com.example.springsecurity.exception.AppException;
//import com.example.springsecurity.exception.NotFoundException;
//import com.example.springsecurity.mapper.OrderMapper;
//import com.example.springsecurity.repository.CustomerDiscountRepository;
//import com.example.springsecurity.repository.OrderRepository;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.CardService;
//import com.example.springsecurity.service.CustomerDiscountService;
//import com.example.springsecurity.service.CustomerService;
//import com.example.springsecurity.service.ProductService;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//
//
//@Service
//@AllArgsConstructor
//public class TestService {
//    @Autowired
//    private CustomerDiscountRepository customerDiscountRepository;
//
//    @Autowired
//    private ProductService productService;
//
//    private CustomerDiscountService customerDiscountService;
//
//    @Transactional
//    public Product getDiscountedProduct(Long customerId, Long productId) {
//        CustomerDiscount customerDiscount = customerDiscountRepository.findByCustomerIdAndProductId(customerId, productId);
//
//        // Проверка наличия и применение скидки
//        if (customerDiscount != null && customerDiscount.getDiscountPrice() != null) {
//            return customerDiscountService.applyDiscountIfDiceRollsAreSix(productId, customerDiscount.getDiscountPrice());
//        } else {
//            // Возвращение исходного продукта, если скидка не применена
//            return productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//}
