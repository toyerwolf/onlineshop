//package com.example.springsecurity.util;
//
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.exception.NotFoundException;
//import com.example.springsecurity.repository.OrderRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import static com.example.springsecurity.entity.Status.CANCEL;
//
//@Component
//@AllArgsConstructor
//public class OrderServiceUtil {
//
//    private final OrderRepository orderRepository;
//
//    public static Order getOrderOrThrow(Long orderId, OrderRepository orderRepository) {
//        return orderRepository.findById(orderId)
//                .orElseThrow(() -> new NotFoundException("Order not found"));
//    }
//
//    public static void validateOrderStatus(Order order) {
//        order.setPaid(true);
//        if (order.isPaid() || order.getStatus() == CANCEL) {
//            throw new IllegalStateException("Order is already paid or cancelled");
//        }
//    }
//}
