package com.example.springsecurity.service.impl;


import com.example.springsecurity.dto.OrderDto;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.SalesStaticDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Status;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.exception.InsufficientQuantityException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.CustomerMapper;
import com.example.springsecurity.mapper.OrderMapper;
import com.example.springsecurity.mapper.ProductMapper;

import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.OrderService;
import com.example.springsecurity.service.ProductService;
import com.example.springsecurity.util.OrderValidationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductService productService;
    private final CustomerService customerService;
    private OrderRepository orderRepository;
    private final ProductMapper productMapper=ProductMapper.INSTANCE;
    private final OrderMapper orderMapper=OrderMapper.INSTANCE;


    @Override
    @Transactional
    public void makeOrder(Long customerId,OrderRequest orderRequest) {
        Customer customer = customerService.findCustomerById(customerId);
        BigDecimal totalAmount = calculateTotalAmount(orderRequest);
        validateCustomerBalance(customer, totalAmount);
        Map<Product, Integer> productQuantities = getProductQuantities(orderRequest.getProductQuantities());
        validateProductQuantities(productQuantities);
        decreaseCustomerBalance(customer.getId(), totalAmount);
        decreaseProductCount(productQuantities);
        Order order = createOrder(customer, totalAmount, productQuantities);
        orderRepository.save(order);
    }

    @Override
    public void markOrderAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        if (order.getStatus() == Status.DELIVERED) {
            throw new RuntimeException("Order is already delivered");
        }
        order.setStatus(Status.DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public List<ProductDto> findProductsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for id: " + orderId));
        return order.getProducts().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::orderToOrderDto)
                .collect(Collectors.toList());
    }


    //String eto nazvanie produkta,a Interger count prodannogo produkta
    public Map<String, Integer> countSoldProductsByYear(int year) {
        List<Object[]> results = orderRepository.countSoldProductsByYear(year);
        Map<String, Integer> soldProductsByYear = new HashMap<>();
        for (Object[] result : results) {
            String productName = (String) result[2];
            Integer totalSold = ((Number) result[1]).intValue();
            soldProductsByYear.put(productName, totalSold);
        }

        return soldProductsByYear;
    }

    public Map<Integer, Integer> getProductSalesStatistics() {
        List<Object[]> salesData = orderRepository.getProductSalesStatistics();
        Map<Integer, Integer> salesByYear = new HashMap<>();
        for (Object[] row : salesData) {
            int year = ((Number) row[0]).intValue();
            int totalSold = ((Number) row[1]).intValue();
            salesByYear.put(year, totalSold);
        }

        return salesByYear;
    }


    public Map<Integer, BigDecimal> getTotalProductSalesRevenueByYear() {
        List<Object[]> salesData = orderRepository.getSoldProductSalesStatistics();
        Map<Integer, BigDecimal> totalRevenueByYear = new HashMap<>();
        for (Object[] row : salesData) {
            int year = ((Number) row[0]).intValue();
            BigDecimal totalRevenue = (BigDecimal) row[1];
            totalRevenueByYear.put(year, totalRevenue);
        }
        return totalRevenueByYear;
    }





    private BigDecimal calculateTotalAmount(OrderRequest orderRequest) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : orderRequest.getProductQuantities().entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            ProductDto productDto = productService.getProductById(productId);
            BigDecimal price = productDto.getDiscountPrice() != null ? productDto.getDiscountPrice() : productDto.getPrice();
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return totalAmount;
    }

    private void validateCustomerBalance(Customer customer, BigDecimal totalAmount) {
        BigDecimal customerBalance = customer.getBalance();
        if (customerBalance.compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    private void validateProductQuantities(Map<Product, Integer> productQuantities) {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            if (product.getQuantity() < quantity) {
                throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName());
            }
        }
    }

    private Map<Product, Integer> getProductQuantities(Map<Long, Integer> productQuantities) {
        Map<Product, Integer> products = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            Product product = productService.findProductById(productId);
            products.put(product, quantity);
        }
        return products;
    }

    private Order createOrder(Customer customer, BigDecimal totalAmount, Map<Product, Integer> productsWithQuantity) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Status.ORDERED);
        order.setProductsWithQuantity(productsWithQuantity);
        return order;
    }

    private void decreaseCustomerBalance(Long customerId, BigDecimal amount) {
        customerService.decreaseBalance(customerId, amount);
    }

    private void decreaseProductCount(Map<Product, Integer> productQuantities) {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            productService.decreaseCount(product.getId(), quantity);
        }
}
}






