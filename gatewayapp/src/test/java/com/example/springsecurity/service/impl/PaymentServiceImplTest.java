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
    private OrderServiceImpl orderService;

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
        // Создание тестовых данных
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCustomerId(1L);
        paymentRequest.setCardId(1L);
        paymentRequest.setOrderId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(100.00));


        when(customerService.findCustomerById(anyLong())).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, paymentRequest.getCardId())).thenReturn(card);
        when(orderService.getOrderOrThrow(paymentRequest.getOrderId())).thenReturn(order);

        // Вызов тестируемого метода
        paymentService.processPaymentWithCard(paymentRequest);

        verify(customerService).findCustomerById(1L);
        verify(customerService).getCustomerCardById(customer, 1L);
        verify(orderService).getOrderOrThrow(1L);
        verify(orderService).validateOrderBelongsToCustomer(order, customer);
        verify(orderService).validateOrder(order);
        verify(cardService).decreaseFromCardBalance(card, BigDecimal.valueOf(100.00));
        verify(paymentService).saveCreditCardPayment(order, customer, BigDecimal.valueOf(100.00));
        verify(orderService).updateOrderStatus(order);
    }

    @Test
    @Transactional
    void processPaymentWithPayPal_Success() {
        // Prepare test data
        Long customerId = 1L;
        Long orderId = 1L;
        Customer customer = new Customer();
        Order order = new Order();
        BigDecimal totalAmount = BigDecimal.valueOf(100.00);
        order.setTotalAmount(totalAmount);

        // Mock the dependencies
        when(orderService.getOrderOrThrow(orderId)).thenReturn(order);
        when(customerService.findCustomerById(customerId)).thenReturn(customer);

        // Call the method under test
        paymentService.processPaymentWithPayPal(customerId, orderId);

        // Verify interactions
        verify(orderService).validateOrderBelongsToCustomer(order, customer);
        verify(orderService).validateOrder(order);
        verify(customerBalanceService).decreaseBalance(customerId, totalAmount);
        verify(orderRepository).save(order);
        verify(paymentService).savePayPalPayment(order, customer, totalAmount);
        verify(orderService).updateOrderStatus(order);
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








