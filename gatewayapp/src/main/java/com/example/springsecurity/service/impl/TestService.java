//package com.example.springsecurity.service.impl;
//
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.entity.Product;
//import com.example.springsecurity.entity.Status;
//import com.example.springsecurity.exception.AppException;
//import com.example.springsecurity.exception.NotFoundException;
//import com.example.springsecurity.mapper.OrderMapper;
//import com.example.springsecurity.repository.OrderRepository;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.CardService;
//import com.example.springsecurity.service.CustomerService;
//import com.example.springsecurity.service.ProductService;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
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
//import static com.example.springsecurity.entity.Status.PAID;
//
//@Service
//@AllArgsConstructor
//public class TestService {
//    private final ProductService productService;
//    private final CustomerService customerService;
//    private OrderRepository orderRepository;
//
//    private final CustomerBalanceService customerBalanceService;
//    private final ProductInventoryService productInventoryService;
//    private final OrderProductRepositorySave orderProductRepositorySave;
//    private final OrderCancellationService orderCancellationService;
//    private static final long DELAY_IN_MINUTES = 1;
//
//
//
//
//
//    @Transactional
//    public void makeOrderTest(Long customerId, OrderRequest orderRequest) {
//        Customer customer = customerService.findCustomerById(customerId);
//        BigDecimal totalAmount = calculateTotalAmount(orderRequest);
//        customerBalanceService.validateCustomerBalance(customer, totalAmount);
//        Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
//        productInventoryService.validateProductQuantities(productQuantities);
//        Order order = createOrder(customer, totalAmount);
//        productInventoryService.decreaseProductCount(productQuantities);
//        orderRepository.save(order);
//        saveOrderProduct(order,productQuantities);
//        scheduleOrderPaymentCheck(order.getId());
//        }
//
//
//
//    private void scheduleOrderPaymentCheck(Long orderId) {
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.schedule(() -> checkOrderPaymentStatus(orderId), TestService.DELAY_IN_MINUTES, TimeUnit.MINUTES);
//    }
//
//    private void checkOrderPaymentStatus(Long orderId) {
//        Order order = orderRepository.findById(orderId).orElse(null);
//        if (order != null && !order.isPaid()) {
//            orderCancellationService.cancelOrder(order.getId());
//        }
//    }
//
//    protected BigDecimal calculateTotalAmount(OrderRequest orderRequest) {
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (Map.Entry<Long, Integer> entry : orderRequest.getProductQuantities().entrySet()) {
//            Long productId = entry.getKey();
//            Integer quantity = entry.getValue();
//            Product product = productService.findProductById(productId);
//            BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
//            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
//        }
//        return totalAmount;
//    }
//
//
//    protected Order createOrder(Customer customer, BigDecimal totalAmount) {
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setTotalAmount(totalAmount);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setStatus(Status.PENDING);
//        return order;
//    }
//
//    protected void saveOrderProduct(Order order, Map<Product, Integer> productQuantities) {
//        orderProductRepositorySave.saveAll(productQuantities, order);
//    }
//
//
//
//}
