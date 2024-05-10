//package com.example.springsecurity.service.impl;
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.entity.CustomerCardDetails;
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.entity.Product;
//import com.example.springsecurity.repository.CustomerRepository;
//import com.example.springsecurity.repository.OrderRepository;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.CustomerService;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@AllArgsConstructor
//public class TestService {
//    private final CardService cardService;
//    private final OrderServiceImpl orderService;
//    private final ProductInventoryService productInventoryService;
//    private final CustomerService customerService;
//    private final OrderRepository orderRepository;
//
//
////    @Transactional
////    public void makeOrder(Long customerId, OrderRequest orderRequest, Long cardId) {
////        Customer customer = customerRepository.findById(customerId)
////                .orElseThrow(() -> new RuntimeException("Customer not found"));
////        List<CustomerCardDetails> cards = customer.getCards();
////
////        CustomerCardDetails card = cards.stream()
////                .filter(c -> c.getId().equals(cardId))
////                .findFirst()
////                .orElseThrow(() -> new RuntimeException("Card not found"));
////        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest);
////        cardService.validateCardBalance(card, totalAmount);
////        Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
////        productInventoryService.validateProductQuantities(productQuantities);
////
////        Order order = orderService.createOrder(customer, totalAmount);
////        orderRepository.save(order);
////        orderService.saveOrder(order, productQuantities);
////    }
//
//    @Transactional
//    public void makeOrderWithCard(Long customerId, OrderRequest orderRequest, Long cardId) {
//        // Находим клиента по ID
//        Customer customer = customerService.findCustomerById(customerId);
//        CustomerCardDetails card = customerService.getCustomerCardById(customer, cardId);
//        // Проверяем баланс карты перед совершением заказа
//        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest);
//        cardService.validateCardBalance(card, totalAmount);
//
//        // Получаем информацию о товарах и их количестве из заказа
//        Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
//        // Проверяем наличие товаров на складе
//        productInventoryService.validateProductQuantities(productQuantities);
//        Order order = orderService.createOrder(customer, totalAmount);
//        orderRepository.save(order);
//        // Создаем и сохраняем товары заказа
//        orderService.saveOrderProduct(order, productQuantities);
//    }
//}
