package com.example.springsecurity.service.impl;


import com.example.springsecurity.dto.DiscountProductResponse;
import com.example.springsecurity.dto.OrderDto;

import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.OrderResponse;
import com.example.springsecurity.entity.*;
;
import com.example.springsecurity.exception.AppException;
import com.example.springsecurity.exception.CarExpiredException;
import com.example.springsecurity.exception.NotFoundException;

import com.example.springsecurity.exception.OrderDeliveredException;
import com.example.springsecurity.mapper.OrderMapper;

import com.example.springsecurity.repository.CustomerDiscountRepository;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.OrderRepository;

import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.*;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.springsecurity.entity.OrderStatus.PAID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductService productService;
    private final CustomerService customerService;
    private OrderRepository orderRepository;
    private final ProductFinderService productFinderService;
    private final OrderMapper orderMapper=OrderMapper.INSTANCE;
    private final CustomerBalanceService customerBalanceService;
    private final ProductInventoryService productInventoryService;
    private final OrderProductRepositorySave orderProductRepositorySave;
    private final CardService cardService;
    private final OrderCancellationService orderCancellationService;
    private final OrderProductService orderProductService;

    private final CustomerDiscountService customerDiscountService;
    private final CustomerFinderService customerFinderService;
    public static final long DELAY_IN_MINUTES = 1;



    @Override
    @Transactional
    public OrderResponse makeOrder(Long customerId, OrderRequest orderRequest) {
        Customer customer = customerFinderService.findCustomerById(customerId);
        Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
        BigDecimal totalAmount = calculateTotalAmount(orderRequest,productQuantities);
        customerBalanceService.validateCustomerBalance(customer, totalAmount);
        productInventoryService.validateProductQuantities(productQuantities);
       Order order = createOrder(customer, totalAmount);
       productInventoryService.decreaseProductCount(productQuantities);
        saveOrder(order);
        saveOrderProduct(order,productQuantities);
        return getOrderResponse(order);
    }

    @NotNull OrderResponse getOrderResponse(Order order) {
        List<OrderProductDto> orderProducts = orderProductService.findOrderProductsByOrderId(order.getId());
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setPaid(order.isPaid());
        orderResponse.setProducts(orderProducts);
        scheduleOrderPaymentCheck(order.getId());
        return orderResponse;
    }

    @Override
    @Transactional
    public OrderResponse makeOrderWithCard(Long customerId, OrderRequest orderRequest, Long cardId) {

        Customer customer = customerFinderService.findCustomerById(customerId);
        CustomerCardDetails card = customerService.getCustomerCardById(customer, cardId);
        if (cardService.isCardExpired(card.getExpirationDate())) {
            throw new CarExpiredException("The card has expired");
        }Map<Product, Integer> productQuantities = productInventoryService.getProductQuantities(orderRequest.getProductQuantities());
        BigDecimal totalAmount = calculateTotalAmount(orderRequest, productQuantities);
        cardService.validateCardBalance(card, totalAmount);
        productInventoryService.validateProductQuantities(productQuantities);
        Order order = createOrder(customer, totalAmount);
        productInventoryService.decreaseProductCount(productQuantities);
        saveOrder(order);
        saveOrderProduct(order, productQuantities);
        return getOrderResponse(order);
    }




    void scheduleOrderPaymentCheck(Long orderId) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> checkOrderPaymentStatus(orderId), OrderServiceImpl.DELAY_IN_MINUTES, TimeUnit.MINUTES);
    }

    void checkOrderPaymentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && !order.isPaid()) {
           orderCancellationService.cancelOrder(order.getId());
        }
    }

    @Override
    public void markOrderAsDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderDeliveredException("Order is already delivered");
        }
        order.setStatus(OrderStatus.DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }



    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::orderToOrderDto)
                .collect(Collectors.toList());
    }





    @Override
    public List<OrderDto> findOrdersByCustomerID(Long customerId) {
        List<Order> orders = orderRepository.findAllByCustomer_Id(customerId);
        return orderMapper.ordersToOrderDtos(orders);
    }





    //proxodimsa po mapu,berem id  kajdogo prodcuta i kolichestvo kajdogo produkta
//    esli est discount price ispolzuyem ego esli netu sam price,i umnojayem na count
    protected BigDecimal calculateTotalAmount(OrderRequest orderRequest, Map<Product, Integer> productMap) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : productMap.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            totalAmount = totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        return totalAmount;
    }

     protected Order createOrder(Customer customer, BigDecimal totalAmount) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return order;
    }

    protected void saveOrderProduct(Order order, Map<Product, Integer> productQuantities) {
        orderProductRepositorySave.saveAll(productQuantities, order);
    }

    protected Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
    }

    protected void validateOrder(Order order) {
        if (order.isPaid()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Order is already paid");
        }
        if (order.getStatus() == OrderStatus.CANCEL) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Order status is cancel");
        }
    }

   protected void updateOrderStatus(Order order) {
        order.setStatus(PAID);
        order.setPaid(true);
        orderRepository.save(order);
    }

    void validateOrderBelongsToCustomer(Order order, Customer customer) {
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Order does not belong to the specified customer");
        }
}

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }


    @Transactional
    public OrderResponse makeOrderForDiscountedProduct(Long customerId, Long productId) {
        // Найти информацию о скидке для выигранного продукта
        DiscountProductResponse discountProductResponse = customerDiscountService.getDiscountedProductResponse(customerId);
        Customer customer = customerFinderService.findCustomerById(customerId);
        Product product = productFinderService.findProductById(productId);
        if (!"iphone 15".equals(product.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Discount is only applicable for iPhone 15");
        }
        customerBalanceService.validateCustomerBalance(customer,discountProductResponse.getDiscountedPrice());
        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product, 1); // Проверяем наличие одного продукта
        productInventoryService.validateProductQuantities(productQuantities);
        productInventoryService.decreaseProductCount(productQuantities);// Уменьшаем на один продукт
        Order order = createOrder(customer, discountProductResponse.getDiscountedPrice());
        saveOrderProduct(order, productQuantities);
        saveOrder(order);
        return getOrderResponse(order);
}


}







