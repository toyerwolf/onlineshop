//package com.example.springsecurity.service.impl;
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.entity.Product;
//import com.example.springsecurity.entity.Status;
//import com.example.springsecurity.repository.OrderRepository;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.CustomerService;
//import com.example.springsecurity.service.ProductService;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Service
//@AllArgsConstructor
//public class OrderServiceTest {
//
//    private final OrderRepository orderRepository;
//    private final ProductInventoryService productInventoryService;
//    private final OrderProductRepositorySave orderProductRepositorySave;
//    private final ProductService productService;
//    private final CustomerService customerService;
//    private final CustomerBalanceService customerBalanceService;
//
//
//
//    @Transactional
//    public void makeOrder(Long customerId, OrderRequest orderRequest) {
//        Customer customer = customerService.findCustomerById(customerId);
//        BigDecimal totalAmount = calculateTotalAmount(orderRequest);
//        customerBalanceService.validateCustomerBalance(customer, totalAmount);
//        Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
//        productInventoryService.validateProductQuantities(productQuantities);
//        Order order = createOrder(customer, totalAmount);
//        orderRepository.save(order);
////        saveOrder(order, productQuantities);
//    }
//
//
//
//
//
//    protected Order createOrder(Customer customer, BigDecimal totalAmount) {
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setTotalAmount(totalAmount);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setStatus(Status.ORDERED);
//        return order;
//    }
//
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
//
//    private void saveOrder(Order order, Map<Product, Integer> productQuantities) {
//        productInventoryService.decreaseProductCount(productQuantities);
//        orderProductRepositorySave.saveAll(productQuantities, order);
//    }
//}

//
//    protected void validateCustomerBalance(Customer customer, BigDecimal totalAmount) {
//        BigDecimal customerBalance = customer.getBalance();
//        if (customerBalance.compareTo(totalAmount) < 0) {
//            throw new InsufficientBalanceException("Insufficient balance");
//        }
//    }
//
////    key sam produkt,a value kolichestvo
//     protected void validateProductQuantities(Map<Product, Integer> productQuantities) {
//        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
//            Product product = entry.getKey();
//            Integer quantity = entry.getValue();
//            if (product.getQuantity() < quantity) {
//                throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName());
//            }
//        }
//    }


//
//
////    prinimayet map s id producta i kolichestvo kajdogo produkta
//   protected Map<Product, Integer> getProductQuantities(Map<Long, Integer> productQuantities) {
//        Map<Product, Integer> products = new HashMap<>();
//        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
//            Long productId = entry.getKey();
//            Integer quantity = entry.getValue();
//            Product product = productService.findProductById(productId);
//            products.put(product, quantity);
//        }
//        return products;
//    }
//

//
//    protected void decreaseCustomerBalance(Long customerId, BigDecimal amount) {
//        customerService.decreaseBalance(customerId, amount);
//    }
//
//    protected void decreaseProductCount(Map<Product, Integer> productQuantities) {
//        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
//            Product product = entry.getKey();
//            Integer quantity = entry.getValue();
//            productService.decreaseCount(product.getId(), quantity);
//        }
//    }
//
//    void saveOrderProducts(Order order, Map<Product, Integer> productQuantities) {
//        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
//            Product product = entry.getKey();
//            Integer quantity = entry.getValue();
//            OrderProduct orderProduct = new OrderProduct();
//            orderProduct.setOrder(order);
//            orderProduct.setProduct(product);
//            orderProduct.setQuantity(quantity);
//            orderProduct.setProductName(product.getName());
//            orderProductRepository.save(orderProduct);
//        }

