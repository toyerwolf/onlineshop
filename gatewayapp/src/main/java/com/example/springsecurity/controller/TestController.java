//package com.example.springsecurity.controller;//package com.example.springsecurity.controller;
////
////import com.example.springsecurity.req.CustomerCardReq;
////import com.example.springsecurity.req.OrderRequest;
////import com.example.springsecurity.service.impl.CardServiceImpl;
////import com.example.springsecurity.service.impl.TestService;
////import lombok.AllArgsConstructor;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
//
//import com.example.springsecurity.dto.DiscountProductResponse;
//import com.example.springsecurity.service.CustomerDiscountService;
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("test")
//@AllArgsConstructor
//public class TestController {
//
//    private final CustomerDiscountService customerDiscountService;
//
//
//    @GetMapping("/discounted-product")
//    public ResponseEntity<DiscountProductResponse> getDiscountedProduct(@RequestParam Long customerId) {
//        DiscountProductResponse response = customerDiscountService.getDiscountedProductResponse(customerId);
//        return ResponseEntity.ok(response);
//    }
//}