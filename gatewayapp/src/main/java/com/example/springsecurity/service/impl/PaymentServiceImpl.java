package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.*;
import com.example.springsecurity.repository.OrderProductRepository;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.PaymentRepository;
import com.example.springsecurity.req.PaymentRequest;
import com.example.springsecurity.service.CardService;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;


@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderServiceImpl orderServiceimpl;


    private final CustomerService customerService;
    private final CardService cardService;
    private final PaymentRepository paymentRepository;
    private final CustomerBalanceService customerBalanceService;
    private final OrderRepository orderRepository;




    @Override
    @Transactional
        public void processPaymentWithCard(PaymentRequest paymentRequest) {
        Customer customer = customerService.findCustomerById(paymentRequest.getCustomerId());
        CustomerCardDetails card = customerService.getCustomerCardById(customer, paymentRequest.getCardId());
        Order order = orderServiceimpl.getOrderOrThrow(paymentRequest.getOrderId());
        orderServiceimpl.validateOrderBelongsToCustomer(order, customer);
        orderServiceimpl.validateOrder(order);
        cardService.decreaseFromCardBalance(card, order.getTotalAmount());
        saveCreditCardPayment(order, customer, order.getTotalAmount());
        orderServiceimpl.updateOrderStatus(order);
    }

    @Override
    @Transactional
    public void processPaymentWithPayPal(Long customerId, Long orderId) {
        Order order = orderServiceimpl.getOrderOrThrow(orderId);
        orderServiceimpl.validateOrderBelongsToCustomer(order, customerService.findCustomerById(customerId));
        orderServiceimpl.validateOrder(order);
        Customer customer = customerService.findCustomerById(customerId);
        BigDecimal totalAmount = order.getTotalAmount();
        customerBalanceService.decreaseBalance(customerId, totalAmount);
        orderRepository.save(order);
        savePayPalPayment(order, customer, totalAmount);
        orderServiceimpl.updateOrderStatus(order);
    }




    public void saveCreditCardPayment(Order order, Customer customer, BigDecimal amount) {
        Payment payment = createPayment(order, customer, amount, PaymentMethod.CREDIT_CARD);
        paymentRepository.save(payment);
    }

    public void savePayPalPayment(Order order, Customer customer, BigDecimal amount) {
        Payment payment = createPayment(order, customer, amount, PaymentMethod.PAYPAL);
        paymentRepository.save(payment);
    }

    Payment createPayment(Order order, Customer customer, BigDecimal amount, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setPaymentStatus(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        return payment;
    }


}
