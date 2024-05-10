package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.entity.*;
import com.example.springsecurity.exception.AppException;
import com.example.springsecurity.exception.CarExpiredException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.exception.OrderDeliveredException;
import com.example.springsecurity.mapper.OrderMapper;
import com.example.springsecurity.repository.OrderProductRepository;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.CardService;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @InjectMocks
    @Spy
    private OrderServiceImpl orderService;

    @Mock
    private  ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductRepositorySave orderProductRepositorySave;

    @Mock
    private CustomerService customerService;

    @Mock
    private CardService cardService;

    @Mock
    private ProductInventoryService productInventoryService;




    @Mock
    private OrderCancellationService orderCancellationService;


    @Mock
    private OrderMapper orderMapper;



    @Test
    void testCalculateTotalAmount() {
        // Act
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2); // Продукт с id = 1 в количестве 2
        productQuantities.put(2L, 1); // Продукт с id = 2 в количестве 1
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        // Мокируем productService.findProductById для каждого продукта
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.TEN); // Устанавливаем цену продукта
        when(productService.findProductById(1L)).thenReturn(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setDiscountPrice(BigDecimal.valueOf(8)); // Устанавливаем скидочную цену продукта
        when(productService.findProductById(2L)).thenReturn(product2);

        // Вызываем метод, который тестируем
        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest);

        BigDecimal expectedTotalAmount = BigDecimal.valueOf(28);

        // Проверяем, что результат соответствует ожидаемому значению
        assertEquals(expectedTotalAmount, totalAmount);
    }

    @Test
    void testCalculateTotalAmount_NoDiscount() {
        // Act
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 3); // Продукт с id = 1 в количестве 3
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        // Мокируем productService.findProductById для каждого продукта
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(15)); // Устанавливаем цену продукта
        when(productService.findProductById(1L)).thenReturn(product1);

        // Вызываем метод, который тестируем
        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest);

        BigDecimal expectedTotalAmount = BigDecimal.valueOf(45);

        // Проверяем, что результат соответствует ожидаемому значению
        assertEquals(expectedTotalAmount, totalAmount);
    }

    @Test
    void testCreateOrder() {
        // Act
        Customer customer = new Customer();
        BigDecimal totalAmount = BigDecimal.valueOf(100);



        Order order = orderService.createOrder(customer, totalAmount);

        // Assert
        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals(totalAmount, order.getTotalAmount());
        assertNotNull(order.getCreatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testSaveOrderProduct() {
        // Act
        Order order = new Order();
        Map<Product, Integer> productQuantities = new HashMap<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test");
        //Assert
        productQuantities.put(product, 2);
        orderService.saveOrderProduct(order, productQuantities);
        verify(orderProductRepositorySave, times(1)).saveAll(productQuantities, order);
    }

    @Test
    void testGetOrderOrThrow_OrderExists() {
        // Act
        Long orderId = 1L;
        Order expectedOrder = new Order();

        // Mock repository
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        // Вызов метода
        Order order = orderService.getOrderOrThrow(orderId);

        // Проверка
        assertNotNull(order);
        assertEquals(expectedOrder, order);
    }

    @Test
    void testGetOrderOrThrow_OrderNotExists() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderOrThrow(orderId));
    }

    @Test
    void testValidateOrder_OrderNotPaidAndNotCancelled() {
        // Act
        Order order = new Order();
        order.setPaid(false);
        order.setStatus(OrderStatus.PENDING);

        // Вызов метода
        assertDoesNotThrow(() -> orderService.validateOrder(order));
    }

    @Test
    void testValidateOrder_OrderAlreadyPaid() {
        // Act
        Order order = new Order();
        order.setPaid(true);
        AppException exception = assertThrows(AppException.class, () -> orderService.validateOrder(order));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Order is already paid", exception.getMessage());
    }

    @Test
    void testValidateOrder_OrderCancelled() {
        Order order = new Order();
        order.setStatus(OrderStatus.CANCEL);
        AppException exception = assertThrows(AppException.class, () -> orderService.validateOrder(order));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Order status is cancel", exception.getMessage());
    }


    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        orderService.updateOrderStatus(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(order.isPaid());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testValidateOrderBelongsToCustomer_OrderBelongsToCustomer() {
        Order order = new Order();
        Customer customer = new Customer();
        customer.setId(1L);
        order.setCustomer(customer);


        assertDoesNotThrow(() -> orderService.validateOrderBelongsToCustomer(order, customer));
    }

    @Test
    void testValidateOrderBelongsToCustomer_OrderDoesNotBelongToCustomer() {
        Order order = new Order();
        Customer customer = new Customer();
        customer.setId(1L);
        order.setCustomer(customer);
        Customer differentCustomer = new Customer();
        differentCustomer.setId(2L);
        AppException exception = assertThrows(AppException.class, () -> orderService.validateOrderBelongsToCustomer(order, differentCustomer));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Order does not belong to the specified customer", exception.getMessage());
    }



    @Test
    void makeOrderWithValidCard() {
        //Act
        Customer customer = new Customer();
        customer.setId(1L);
        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(1L);
        card.setExpirationDate(LocalDate.now().plusYears(1));

        OrderRequest orderRequest = new OrderRequest();
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2);
        orderRequest.setProductQuantities(productQuantities);
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("100.00"));


        when(customerService.findCustomerById(anyLong())).thenReturn(customer);
        when(customerService.getCustomerCardById(eq(customer), anyLong())).thenReturn(card);
        when(cardService.isCardExpired(card.getExpirationDate())).thenReturn(false);
        when(productService.findProductById(productId)).thenReturn(product);
        orderService.makeOrderWithCard(1L, orderRequest, 1L);
        verify(customerService).findCustomerById(1L);
        verify(customerService).getCustomerCardById(customer, 1L);
        verify(cardService).isCardExpired(card.getExpirationDate());
        verify(productInventoryService).validateProductQuantities(anyMap());
        verify(productInventoryService).decreaseProductCount(anyMap());
        verify(orderRepository).save(any(Order.class));
        verify(orderProductRepositorySave).saveAll(anyMap(), any(Order.class));
    }

    @Test
    void makeOrderWithExpiredCard() {
        Long customerId = 1L;
        Long cardId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(cardId);
        card.setExpirationDate(LocalDate.now().minusDays(1));

        OrderRequest orderRequest = new OrderRequest();

        when(customerService.findCustomerById(customerId)).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, cardId)).thenReturn(card);
        when(cardService.isCardExpired(card.getExpirationDate())).thenReturn(true);

        // Вызов метода, ожидаем выброс исключения
        assertThrows(CarExpiredException.class, () -> orderService.makeOrderWithCard(customerId, orderRequest, cardId));

        // Проверка взаимодействия
        verify(customerService).findCustomerById(customerId);
        verify(customerService).getCustomerCardById(customer, cardId);
        verify(cardService).isCardExpired(card.getExpirationDate());

    }

    @Test
    void makeOrder_success_Order() {

        //Act
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        OrderRequest orderRequest = new OrderRequest();
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2);
        orderRequest.setProductQuantities(productQuantities);

        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("100.00"));

        Order order = new Order();
        order.setId(1L);

        when(customerService.findCustomerById(anyLong())).thenReturn(customer);
        when(productService.findProductById(productId)).thenReturn(product);
        when(orderService.createOrder(any(Customer.class), any(BigDecimal.class))).thenReturn(order);

        orderService.makeOrder(customerId, orderRequest);

        verify(customerService).findCustomerById(customerId);
        verify(productInventoryService).validateProductQuantities(anyMap());
        verify(productInventoryService).decreaseProductCount(anyMap());
        verify(orderRepository).save(any(Order.class));
        verify(orderProductRepositorySave).saveAll(anyMap(), any(Order.class));
        verify(orderService, times(1)).scheduleOrderPaymentCheck(order.getId());
    }

    @Test
    public void scheduleOrderPaymentCheck_Test() {
        //sozdayem mock obyekta  OrderServiceImpl
        OrderServiceImpl orderService = mock(OrderServiceImpl.class);
        //vizivayem sam metod
        orderService.scheduleOrderPaymentCheck(123L);
        // proverka vizvan li metod s pravilnimi arqumentami
        verify(orderService, times(1)).scheduleOrderPaymentCheck(123L);
        //proverka chto sozdan ScheduledExecutorService i vizvan metod schedule
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> orderService.checkOrderPaymentStatus(123L), OrderServiceImpl.DELAY_IN_MINUTES, TimeUnit.MINUTES);
    }


   // создаем заказ s paid false и проверяем вызовется ли метод cancelOrder
    @Test
    void checkOrderPaymentStatus_OrderExistsAndNotPaid_CancelsOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setPaid(false);
        // Mock
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.checkOrderPaymentStatus(1L);
        verify(orderCancellationService, times(1)).cancelOrder(1L);
    }


    // создаем заказ s paid true и проверяем с never что  метод cancelOrder не вызовется
    @Test
    void checkOrderPaymentStatus_OrderExistsAndPaid_DoesNotCancelOrder() {
        Order order = new Order();
        order.setId(3L);
        order.setPaid(true);
        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        orderService.checkOrderPaymentStatus(3L);
        verify(orderCancellationService, never()).cancelOrder(anyLong());
    }


//находим order,оrder не найден,Проверка, что метод cancelOrder не был вызван
    @Test
    void checkOrderPaymentStatus_OrderNotExists_DoesNotCancelOrder() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        orderService.checkOrderPaymentStatus(2L);
        verify(orderCancellationService, never()).cancelOrder(anyLong());
    }


    //проверям что статус не delivered,потом вызываем метод и проверяем равны ли статусы
    @Test
    void markOrderAsDelivered_OrderExistsAndNotDelivered_StatusUpdatedToDelivered() {
        // Создаем тестовый заказ
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING); // Не доставлен
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.markOrderAsDelivered(1L);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
        verify(orderRepository, times(1)).save(order);
    }


    //order не найден выбрасываем exception
    @Test
    void markOrderAsDelivered_OrderNotFound_ThrowsNotFoundException() {
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.markOrderAsDelivered(2L));
        verify(orderRepository, never()).save(any());
    }


    //когда уже статус установлен delivered выбрасываем exception
    @Test
    void markOrderAsDelivered_OrderAlreadyDelivered_ThrowsOrderDeliveredException() {
        Order order = new Order();
        order.setId(3L);
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        assertThrows(OrderDeliveredException.class, () -> orderService.markOrderAsDelivered(3L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAllOrders_ReturnsListOfOrderDto() {
        // Создаем  данные для заказов
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        List<Order> orders = List.of(order1, order2);
        OrderDto orderDto1 = new OrderDto();
        orderDto1.setId(1L);
        OrderDto orderDto2 = new OrderDto();
        orderDto2.setId(2L);
        List<OrderDto> expectedOrderDtos = List.of(orderDto1, orderDto2);
        when(orderRepository.findAll()).thenReturn(orders);


//        when(orderMapper.orderToOrderDto(order1)).thenReturn(orderDto1);
//        when(orderMapper.orderToOrderDto(order2)).thenReturn(orderDto2);
        List<OrderDto> actualOrderDtos = orderService.getAllOrders();
        verify(orderRepository, times(1)).findAll();
        assertEquals(expectedOrderDtos, actualOrderDtos);
    }


    @Test
    void getAllOrders_ReturnsEmptyList_WhenNoOrdersFound() {
        // Создаем пустой список заказов
        List<Order> orders = new ArrayList<>();
        when(orderRepository.findAll()).thenReturn(orders);
        // Вызываем метод
        List<OrderDto> actualOrderDtos = orderService.getAllOrders();
        // Проверяем, что метод findAll был вызван
        verify(orderRepository, times(1)).findAll();
        // Проверяем, что возвращается пустой список
        assertEquals(0, actualOrderDtos.size());
    }



    @Test
    void testFindOrdersByCustomerID() {
        // Arrange
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        Order order1 = new Order();
        order1.setCustomer(customer);
        Order order2 = new Order();
        order2.setCustomer(customer);

        List<Order> orders = Arrays.asList(order1, order2);

        OrderDto orderDto1 = new OrderDto();
        orderDto1.setCustomerId(customerId);
        OrderDto orderDto2 = new OrderDto();
        orderDto2.setCustomerId(customerId);

        when(orderRepository.findAllByCustomer_Id(customerId)).thenReturn(orders);
//        when(orderMapper.ordersToOrderDtos(orders)).thenReturn(Arrays.asList(orderDto1, orderDto2));

        // Act
        List<OrderDto> result = orderService.findOrdersByCustomerID(customerId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(orderDto1, result.get(0));
        assertEquals(orderDto2, result.get(1));
    }

    @Test
    void testFindOrdersByCustomerID_NoOrders() {
        // Arrange
        Long customerId = 1L;
        User customer = new User();
        customer.setId(customerId);

        List<Order> orders = Collections.emptyList();

        when(orderRepository.findAllByCustomer_Id(customerId)).thenReturn(orders);

        // Act
        List<OrderDto> result = orderService.findOrdersByCustomerID(customerId);

        // Assert
        assertTrue(result.isEmpty());
    }



    }




































