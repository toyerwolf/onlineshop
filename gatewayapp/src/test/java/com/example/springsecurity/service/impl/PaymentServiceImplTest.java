package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.*;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.PaymentRepository;
import com.example.springsecurity.req.PaymentRequest;
import com.example.springsecurity.service.CardService;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.OrderService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private  CustomerService customerService;

    @Mock
    private  CardService cardService;

    @Mock
    private  PaymentRepository paymentRepository;

    @Mock
    private CustomerBalanceService customerBalanceService;

    @Mock
    private  OrderRepository orderRepository;



    //ispolzuyem spy potomu metod kotoriy testiruyem naxoditsa v payment service i vspomoqatalnie toje,
    // dlya verify metodov nujen mock,a spy etot chastichnoe mockriovanie
    @InjectMocks
    @Spy
    private PaymentServiceImpl paymentService;

    @Mock
    private OrderServiceImpl orderServiceImpl;






    @Test
    void processPaymentWithCard_Success() {
        // Arrange
        Long customerId = 1L;
        Long cardId = 1L;
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCustomerId(customerId);
        paymentRequest.setCardId(cardId);
        paymentRequest.setOrderId(orderId);

        Customer customer = new Customer();
        customer.setId(customerId);

        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(cardId);

        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(BigDecimal.valueOf(100.00));

        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, cardId)).thenReturn(card);
        when(orderServiceImpl.getOrderOrThrow(orderId)).thenReturn(order);

        // Act
        paymentService.processPaymentWithCard(paymentRequest);

        // Assert
        verify(customerService).findCustomerById(customerId);
        verify(customerService).getCustomerCardById(customer, cardId);
        verify(orderServiceImpl).getOrderOrThrow(orderId);
        verify(orderServiceImpl).validateOrderBelongsToCustomer(order, customer);
        verify(orderServiceImpl).validateOrder(order);
        verify(cardService).decreaseFromCardBalance(card, order.getTotalAmount());
        verify(paymentService).saveCreditCardPayment(order, customer, order.getTotalAmount());
        verify(orderServiceImpl).updateOrderStatus(order);
    }

    @Test
    void testProcessPaymentWithPayPal() {
        // Arrange
        Long customerId = 1L;
        Long orderId = 100L;
        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(BigDecimal.valueOf(50.0));
        Customer customer = new Customer();
        customer.setId(customerId);

        // Mocking methods
        when(orderServiceImpl.getOrderOrThrow(orderId)).thenReturn(order);
        when(customerService.findCustomerById(customerId)).thenReturn(customer);

        // Act
        paymentService.processPaymentWithPayPal(customerId, orderId);

        // Assert
        verify(orderServiceImpl).validateOrderBelongsToCustomer(order, customer);
        verify(orderServiceImpl).validateOrder(order);
        verify(customerBalanceService).decreaseBalance(customerId, order.getTotalAmount());
        verify(orderRepository).save(order);
        verify(orderServiceImpl).updateOrderStatus(order);
    }


    @Test
    void testSaveCreditCardPayment() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        BigDecimal amount = new BigDecimal("100.00");

        // Act
        paymentService.saveCreditCardPayment(order, customer, amount);

        // Assert
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment capturedPayment = paymentCaptor.getValue();
        assertNotNull(capturedPayment);
        assertEquals(order, capturedPayment.getOrder());
        assertEquals(customer, capturedPayment.getCustomer());
        assertEquals(amount, capturedPayment.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, capturedPayment.getPaymentStatus());
        assertNotNull(capturedPayment.getPaymentDate());
    }


    @Test
    void testSavePayPalPayment() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        BigDecimal amount = new BigDecimal("100.00");

        // Act
        paymentService.savePayPalPayment(order, customer, amount);

        // Assert
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment capturedPayment = paymentCaptor.getValue();
        assertNotNull(capturedPayment);
        assertEquals(order, capturedPayment.getOrder());
        assertEquals(customer, capturedPayment.getCustomer());
        assertEquals(amount, capturedPayment.getAmount());
        assertEquals(PaymentMethod.PAYPAL, capturedPayment.getPaymentStatus());
        assertNotNull(capturedPayment.getPaymentDate());
    }

    @Test
    void testCreatePayment() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        BigDecimal amount = new BigDecimal("100.00");
        PaymentMethod paymentMethod = PaymentMethod.PAYPAL;

        // Act
        Payment payment = paymentService.createPayment(order, customer, amount, paymentMethod);

        // Assert
        assertNotNull(payment);
        assertEquals(order, payment.getOrder());
        assertEquals(customer, payment.getCustomer());
        assertEquals(amount, payment.getAmount());
        assertEquals(paymentMethod, payment.getPaymentStatus());
        assertNotNull(payment.getPaymentDate());
        // Additional assertions if needed
    }
}








