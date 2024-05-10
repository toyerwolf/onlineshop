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
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.req.PaymentRequest;
//import com.example.springsecurity.service.impl.PaymentServiceImpl;
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("test")
//@AllArgsConstructor
//public class TestController {
//
//    private final PaymentServiceImpl paymentServiceImpl;
//    private final TestService testService;
//
////    private final TestService testService;
////
////    private final CardServiceImpl cardService;
//
////    @PostMapping("/{customerId}/cards")
////    public ResponseEntity<Void> addCardToCustomer(
////            @PathVariable Long customerId,
////            @RequestBody CustomerCardReq customerCardReq) {
////
////        cardService.addCardToCustomer(customerId, customerCardReq);
////        return ResponseEntity.status(HttpStatus.CREATED).build();
////    }
////
////    @PostMapping("/make")
////    public ResponseEntity<?> makeOrder(@RequestParam Long customerId,
////                                       @RequestParam Long cardId,
////                                       @RequestBody OrderRequest orderRequest) {
////        testService.makeOrderWithCard(customerId, orderRequest, cardId);
////        return ResponseEntity.ok("Order successfully placed");
////    }
////
////
//
//    @PostMapping
//    public ResponseEntity<Void> processPayment(@RequestBody PaymentRequest paymentRequest) {
//        paymentServiceImpl.processPaymentWithCard(paymentRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @PostMapping("{customerId}/make-order")
//    public ResponseEntity<String> makeOrder(@PathVariable("customerId") Long customerId,
//                                            @RequestBody OrderRequest orderRequest) {
//        try {
//            testService.makeOrderTest(customerId, orderRequest);
//            return ResponseEntity.ok("Order created successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order: " + e.getMessage());
//        }
//    }
//}
