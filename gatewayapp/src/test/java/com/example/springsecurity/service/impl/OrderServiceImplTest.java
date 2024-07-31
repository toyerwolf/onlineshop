package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.DiscountProductResponse;
import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.OrderResponse;
import com.example.springsecurity.entity.*;
import com.example.springsecurity.exception.*;
import com.example.springsecurity.mapper.OrderMapper;
import com.example.springsecurity.repository.OrderProductRepository;
import com.example.springsecurity.repository.OrderRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.*;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ProductService productService;



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
    private ProductFinderService productFinderService;




    @Mock
    private OrderCancellationService orderCancellationService;



    @Mock
    private OrderProductService orderProductService;
    @Mock
    private CustomerBalanceService customerBalanceService;

    @Mock
    private CustomerDiscountService customerDiscountService;

    @Mock
    private CustomerFinderService customerFinderService;





    @Test
    void testCalculateTotalAmount() {
        // Arrange
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2);
        productQuantities.put(2L, 1);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);


        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.TEN); // 10


        Product product2 = new Product();
        product2.setId(2L);
        product2.setDiscountPrice(BigDecimal.valueOf(8)); // 8


        // Создание Map<Product, Integer> из productQuantities
        Map<Product, Integer> productMap = new HashMap<>();
        productMap.put(product1, 2); // 2 * 10 = 20
        productMap.put(product2, 1); // 1 * 8 = 8

        // Act
        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest, productMap);

        // Assert
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(28); // 20 + 8
        assertEquals(expectedTotalAmount, totalAmount);
    }

    @Test
    void testCalculateTotalAmount_NoDiscount() {
        // Arrange
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 3); // Продукт с id = 1 в количестве 3
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        // Создание продукта с заданной ценой
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(15)); // Устанавливаем цену продукта
//        when(productFinderService.findProductById(1L)).thenReturn(product1);

        // Создание Map<Product, Integer> из productQuantities
        Map<Product, Integer> productMap = new HashMap<>();
        productMap.put(product1, 3); // 3 * 15

        // Act
        BigDecimal totalAmount = orderService.calculateTotalAmount(orderRequest, productMap);

        // Assert
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(45); // 3 * 15
        assertEquals(expectedTotalAmount, totalAmount);
    }

    @Test
    void testCreateOrder() {
        // Act
        Customer customer = new Customer();
        BigDecimal totalAmount = BigDecimal.valueOf(100);



        Order order = orderService.createOrder(customer, totalAmount);


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
    @Transactional
    void testMakeOrderWithValidCard() {
        // Arrange
        Long customerId = 1L;
        Long cardId = 1L;
        Long productId = 1L;
        BigDecimal totalAmount = new BigDecimal("200.00");

        Customer customer = new Customer();
        customer.setId(customerId);

        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(cardId);
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setCardBalance(new BigDecimal("1000.00"));

        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(3);

        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(productId, 2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);

        // Set up mocks
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, cardId)).thenReturn(card);
        when(cardService.isCardExpired(card.getExpirationDate())).thenReturn(false);
        when(orderService.calculateTotalAmount(orderRequest, Map.of(product, 2))).thenReturn(totalAmount);
        doNothing().when(cardService).validateCardBalance(card, totalAmount);
        when(productInventoryService.getProductQuantities(productQuantities)).thenReturn(Map.of(product, 2));
        doNothing().when(productInventoryService).validateProductQuantities(anyMap());
        doNothing().when(productInventoryService).decreaseProductCount(anyMap());
        when(orderService.createOrder(customer, totalAmount)).thenReturn(order);
        doNothing().when(orderService).saveOrder(order);

        List<OrderProductDto> orderProductDtos = getOrderProductDtos();
        when(orderProductService.findOrderProductsByOrderId(order.getId())).thenReturn(orderProductDtos);

        // Act
        OrderResponse orderResponse = orderService.makeOrderWithCard(customerId, orderRequest, cardId);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
        assertEquals(order.getTotalAmount(), orderResponse.getTotalAmount());
        assertEquals(order.getCreatedAt(), orderResponse.getCreatedAt());
        assertEquals(order.getStatus(), orderResponse.getStatus());
        assertEquals(order.isPaid(), orderResponse.isPaid());

        assertNotNull(orderResponse.getProducts());
        assertEquals(orderProductDtos.size(), orderResponse.getProducts().size());
        for (int i = 0; i < orderProductDtos.size(); i++) {
            OrderProductDto expectedDto = orderProductDtos.get(i);
            OrderProductDto actualDto = orderResponse.getProducts().get(i);
            assertEquals(expectedDto.getOrderId(), actualDto.getOrderId());
            assertEquals(expectedDto.getProductName(), actualDto.getProductName());
            assertEquals(expectedDto.getQuantity(), actualDto.getQuantity());
        }

        // Verify interactions
        verify(customerFinderService).findCustomerById(customerId);
        verify(customerService).getCustomerCardById(customer, cardId);
        verify(cardService).isCardExpired(card.getExpirationDate());
        verify(cardService).validateCardBalance(card, totalAmount);
        verify(productInventoryService).getProductQuantities(productQuantities);
        verify(productInventoryService).validateProductQuantities(anyMap());
        verify(productInventoryService).decreaseProductCount(anyMap());
        verify(orderService).createOrder(customer, totalAmount);
        verify(orderService).saveOrder(order);
        verify(orderProductService).findOrderProductsByOrderId(order.getId());
    }

    @NotNull
    private static List<OrderProductDto> getOrderProductDtos() {
        List<OrderProductDto> orderProductDtos = new ArrayList<>();
        OrderProductDto orderProductDto1 = new OrderProductDto();
        orderProductDto1.setOrderId(1L);
        orderProductDto1.setProductName("Product1");
        orderProductDto1.setQuantity(2);
        orderProductDtos.add(orderProductDto1);
        OrderProductDto orderProductDto2 = new OrderProductDto();
        orderProductDto2.setOrderId(2L);
        orderProductDto2.setProductName("Product2");
        orderProductDto2.setQuantity(1);
        orderProductDtos.add(orderProductDto2);
        return orderProductDtos;
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

        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, cardId)).thenReturn(card);
        when(cardService.isCardExpired(card.getExpirationDate())).thenReturn(true);

        // Вызов метода, ожидаем выброс исключения
        assertThrows(CarExpiredException.class, () -> orderService.makeOrderWithCard(customerId, orderRequest, cardId));

        // Проверка взаимодействия
        verify(customerFinderService).findCustomerById(customerId);
        verify(customerService).getCustomerCardById(customer, cardId);
        verify(cardService).isCardExpired(card.getExpirationDate());

    }

    @Test
    void testMakeOrderWithCard_InsufficientProductQuantity() {
        // Подготовка данных для теста
        Long customerId = 1L;
        Long cardId = 1L;
        Long productId = 1L;

        OrderRequest orderRequest = new OrderRequest();
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(productId, 5); // Запрошено 5 продуктов

        orderRequest.setProductQuantities(productQuantities);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(3);

        Customer customer = new Customer();
        customer.setId(customerId);

        CustomerCardDetails card = new CustomerCardDetails();
        card.setId(cardId);
        card.setExpirationDate(LocalDate.now().plusMonths(1));

        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
        when(customerService.getCustomerCardById(customer, cardId)).thenReturn(card);
        when(cardService.isCardExpired(card.getExpirationDate())).thenReturn(false);
        when(productFinderService.findProductById(productId)).thenReturn(product);

        when(productInventoryService.getProductQuantities(productQuantities)).thenAnswer(invocation -> {
            Map<Long, Integer> requestedQuantities = invocation.getArgument(0);
            Map<Product, Integer> availableProducts = new HashMap<>();
            for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
                Long productIdEntry = entry.getKey();
                Integer requestedQuantity = entry.getValue();
                Product productEntry = productFinderService.findProductById(productIdEntry);
                if (productEntry.getQuantity() < requestedQuantity) {
                    throw new InsufficientQuantityException("Insufficient quantity for product: " + productEntry.getName());
                }
                availableProducts.put(productEntry, productEntry.getQuantity());
            }
            return availableProducts;
        });

        assertThrows(InsufficientQuantityException.class, () -> orderService.makeOrderWithCard(customerId, orderRequest, cardId));
    }

    @Test
    @Transactional
    void testMakeOrder() {
        Customer customer=new Customer();
        Long customerId = 1L;
        customer.setId(customerId);
        BigDecimal totalAmount = new BigDecimal("200.00");

        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 2);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        Long product1Id = 1L;
        Product product = new Product();
        product.setId(product1Id);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(3);

        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaid(false);

        List<OrderProductDto> orderProductDtos = getOrderProductDtos();

        // Set up mocks
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
//        when(productFinderService.findProductById(product1Id)).thenReturn(product);
        when(orderService.calculateTotalAmount(orderRequest, Map.of(product, 2))).thenReturn(totalAmount);
        when(productInventoryService.getProductQuantities(productQuantities)).thenReturn(Map.of(product, 2));
        doNothing().when(productInventoryService).validateProductQuantities(anyMap());
        doNothing().when(productInventoryService).decreaseProductCount(anyMap());
        when(orderService.createOrder(any(Customer.class), any(BigDecimal.class))).thenReturn(order);
        doNothing().when(customerBalanceService).validateCustomerBalance(any(Customer.class), any(BigDecimal.class));
        when(orderProductService.findOrderProductsByOrderId(order.getId())).thenReturn(orderProductDtos);

        // Act
        OrderResponse orderResponse = orderService.makeOrder(customerId, orderRequest);

        // Assert
        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getOrderId());
        assertNotNull(orderResponse.getCreatedAt());
        assertNotNull(orderResponse.getStatus());
        assertNotNull(orderResponse.getProducts());

        assertEquals(order.getId(), orderResponse.getOrderId());
        assertEquals(order.getTotalAmount(), orderResponse.getTotalAmount());
        assertEquals(order.getCreatedAt(), orderResponse.getCreatedAt());
        assertEquals(order.getStatus(), orderResponse.getStatus());
        assertEquals(order.isPaid(), orderResponse.isPaid());

        assertEquals(orderProductDtos.size(), orderResponse.getProducts().size());
        for (int i = 0; i < orderProductDtos.size(); i++) {
            OrderProductDto expectedDto = orderProductDtos.get(i);
            OrderProductDto actualDto = orderResponse.getProducts().get(i);
            assertEquals(expectedDto.getOrderId(), actualDto.getOrderId());
            assertEquals(expectedDto.getProductName(), actualDto.getProductName());
            assertEquals(expectedDto.getQuantity(), actualDto.getQuantity());
        }

        // Verify interactions
        verify(customerFinderService).findCustomerById(customerId);
//        verify(productFinderService).findProductById(product1Id);
        verify(orderService).calculateTotalAmount(orderRequest, Map.of(product, 2));
        verify(productInventoryService).getProductQuantities(productQuantities);
        verify(productInventoryService).validateProductQuantities(anyMap());
        verify(productInventoryService).decreaseProductCount(anyMap());
        verify(orderService).createOrder(any(Customer.class), any(BigDecimal.class));
        verify(orderService).saveOrder(any(Order.class));
        verify(customerBalanceService).validateCustomerBalance(any(Customer.class), any(BigDecimal.class));
        verify(orderProductService).findOrderProductsByOrderId(order.getId());
    }

    @Test
    void testMakeOrder_InsufficientBalance() {
        // Подготовка данных для теста
        Long customerId = 1L;
        Long productId = 1L;
        OrderRequest orderRequest = new OrderRequest();
        Map<Long, Integer> productQuantitiesRequest = new HashMap<>();
        productQuantitiesRequest.put(productId, 2);
        orderRequest.setProductQuantities(productQuantitiesRequest);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("100.00"));
        product.setDiscountPrice(null); // Нет скидки

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setBalance(new BigDecimal("10.00")); // Баланс клиента меньше суммы заказа

        BigDecimal totalAmount = new BigDecimal("200.00"); // Сумма заказа больше баланса

        // Преобразуйте Map<Long, Integer> в Map<Product, Integer>
        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product, 2);

        // Настройка мока
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
        when(productInventoryService.getProductQuantities(orderRequest.getProductQuantities())).thenReturn(productQuantities);
        when(orderService.calculateTotalAmount(orderRequest, productQuantities)).thenReturn(totalAmount);

        // Настройка для выбрасывания исключения при недостаточном балансе
        doThrow(new InsufficientBalanceException("Insufficient funds on the card"))
                .when(customerBalanceService)
                .validateCustomerBalance(customer, totalAmount);

        // Проверка исключения
        InsufficientBalanceException thrownException = assertThrows(InsufficientBalanceException.class, () ->
                orderService.makeOrder(customerId, orderRequest)
        );
        assertEquals("Insufficient funds on the card", thrownException.getMessage());

        // Убедитесь, что методы были вызваны
        verify(customerFinderService).findCustomerById(customerId);
        verify(productInventoryService).getProductQuantities(orderRequest.getProductQuantities());
        verify(orderService).calculateTotalAmount(orderRequest, productQuantities);
        verify(customerBalanceService).validateCustomerBalance(customer, totalAmount);

        // Убедитесь, что методы не вызываются после выбрасывания исключения
        verifyNoMoreInteractions(productInventoryService, customerFinderService, customerBalanceService);
    }

    @Test
    void makeOrder_InsufficientProductQuantity() {
        // Arrange
        Long customerId = 1L;
        OrderRequest orderRequest = new OrderRequest();
        Map<Long, Integer> productQuantities = new HashMap<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(1000.0));
        product1.setQuantity(3);
        productQuantities.put(product1.getId(), 5);

        orderRequest.setProductQuantities(productQuantities);

        Customer customer = new Customer();
        customer.setId(customerId);

        when(productFinderService.findProductById(product1.getId())).thenReturn(product1);
        //здесь перехватываем exception потому что он в другом методе
        when(productInventoryService.getProductQuantities(productQuantities)).thenAnswer(invocation -> {
            Map<Long, Integer> requestedQuantities = invocation.getArgument(0);
            Map<Product, Integer> availableProducts = new HashMap<>();
            for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
                Long productId = entry.getKey();
                Integer requestedQuantity = entry.getValue();
                Product product = productFinderService.findProductById(productId);
                if (product.getQuantity() < requestedQuantity) {
                    throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName());
                }
                availableProducts.put(product, product.getQuantity());
            }
            return availableProducts;
        });
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);

        // Act & Assert
        assertThrows(InsufficientQuantityException.class, () -> {
            orderService.makeOrder(customerId, orderRequest);
        });

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

    @Test
    void testGetOrderResponse() {
        // Arrange
        Long orderId = 1L;
        BigDecimal totalAmount = new BigDecimal("100.00");
        LocalDateTime createdAt = LocalDateTime.now();
        OrderStatus status = OrderStatus.PENDING;
        boolean isPaid = false;

        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(createdAt);
        order.setStatus(status);
        order.setPaid(isPaid);

        List<OrderProductDto> orderProducts = getOrderProductDtos();


        when(orderProductService.findOrderProductsByOrderId(orderId)).thenReturn(orderProducts);
        doNothing().when(orderService).scheduleOrderPaymentCheck(orderId);

        // Act
        OrderResponse orderResponse = orderService.getOrderResponse(order);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(orderId, orderResponse.getOrderId());
        assertEquals(totalAmount, orderResponse.getTotalAmount());
        assertEquals(createdAt, orderResponse.getCreatedAt());
        assertEquals(status, orderResponse.getStatus());
        assertEquals(isPaid, orderResponse.isPaid());
        assertEquals(orderProducts.size(), orderResponse.getProducts().size());

        for (int i = 0; i < orderProducts.size(); i++) {
            OrderProductDto expectedDto = orderProducts.get(i);
            OrderProductDto actualDto = orderResponse.getProducts().get(i);
            assertEquals(expectedDto.getOrderId(), actualDto.getOrderId());
            assertEquals(expectedDto.getProductName(), actualDto.getProductName());
            assertEquals(expectedDto.getQuantity(), actualDto.getQuantity());
        }

        verify(orderProductService).findOrderProductsByOrderId(orderId);
        verify(orderService).scheduleOrderPaymentCheck(orderId);
    }


    @Test
    void testMakeOrderForDiscountedProduct() {
        // Mock data
        Long customerId = 1L;
        Long productId = 2L;
        BigDecimal discountedPrice = BigDecimal.valueOf(900); // Assuming discounted price for iPhone 15

        // Mock responses
        DiscountProductResponse discountProductResponse = new DiscountProductResponse();
        discountProductResponse.setDiscountedPrice(discountedPrice);
        when(customerDiscountService.getDiscountedProductResponse(customerId)).thenReturn(discountProductResponse);

        Customer customer = new Customer();
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);

        Product product = new Product();
        product.setId(productId);
        product.setName("iphone 15");
        when(productFinderService.findProductById(productId)).thenReturn(product);

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product, 1);

        // Mock validations and service calls
        doNothing().when(customerBalanceService).validateCustomerBalance(customer, discountedPrice);
        doNothing().when(productInventoryService).validateProductQuantities(productQuantities);
        doNothing().when(productInventoryService).decreaseProductCount(productQuantities);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);// Mock order ID for verification
        when(orderService.createOrder(customer, discountedPrice)).thenReturn(order);

        doNothing().when(orderProductRepositorySave).saveAll(productQuantities, order);

        // Test method invocation
        OrderResponse orderResponse = orderService.makeOrderForDiscountedProduct(customerId, productId);

        // Assertions
        assertEquals(order.getId(), orderResponse.getOrderId());
        assertEquals(order.getTotalAmount(), orderResponse.getTotalAmount());
        assertEquals(order.getCreatedAt(), orderResponse.getCreatedAt());
        assertEquals(order.getStatus().toString(), orderResponse.getStatus().toString());
        assertEquals(order.isPaid(), orderResponse.isPaid());

        // Verify method invocations
        verify(customerDiscountService).getDiscountedProductResponse(customerId);
        verify(customerFinderService).findCustomerById(customerId);
        verify(productFinderService).findProductById(productId);
        verify(customerBalanceService).validateCustomerBalance(customer, discountedPrice);
        verify(productInventoryService).validateProductQuantities(productQuantities);
        verify(productInventoryService).decreaseProductCount(productQuantities);
        verify(orderService).createOrder(customer, discountedPrice);
        verify(orderProductRepositorySave).saveAll(productQuantities, order);
    }


    @Test
    void testMakeOrderForDiscountedProduct_InvalidProduct() {
        Long customerId = 1L;
        Long productId = 3L; // Assuming productId does not correspond to iPhone 15

        // Mock responses
        Customer customer = new Customer();
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);

        Product product = new Product();
        product.setId(productId);
        product.setName("ipad");
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Test exception
        AppException exception = assertThrows(AppException.class,
                () -> orderService.makeOrderForDiscountedProduct(customerId, productId));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Discount is only applicable for iPhone 15", exception.getMessage());

        // Verify method invocations
        verify(customerFinderService).findCustomerById(customerId);
        verify(productFinderService).findProductById(productId);
        verify(customerBalanceService, never()).validateCustomerBalance(any(), any());
        verify(productInventoryService, never()).validateProductQuantities(any());
        verify(productInventoryService, never()).decreaseProductCount(any());
        verify(orderService, never()).createOrder(any(), any());
        verify(orderProductRepositorySave, never()).saveAll(any(), any());
    }


    @Test
    void testInsufficientBalanceException() {
        Long customerId = 1L;
        Long productId = 2L;
        BigDecimal discountedPrice = BigDecimal.valueOf(900);

        // Mock responses
        DiscountProductResponse discountProductResponse = new DiscountProductResponse();
        discountProductResponse.setDiscountedPrice(discountedPrice);
        when(customerDiscountService.getDiscountedProductResponse(customerId)).thenReturn(discountProductResponse);

        Customer customer = new Customer();
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);

        Product product = new Product();
        product.setId(productId);
        product.setName("iphone 15");
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Mock validation: throw InsufficientBalanceException
        doThrow(new InsufficientBalanceException("Insufficient balance")).when(customerBalanceService).validateCustomerBalance(customer, discountedPrice);

        // Test
        assertThrows(InsufficientBalanceException.class, () -> {
            orderService.makeOrderForDiscountedProduct(customerId, productId);
        });

        // Verify interactions
        verify(customerDiscountService).getDiscountedProductResponse(customerId);
        verify(customerFinderService).findCustomerById(customerId);
        verify(productFinderService).findProductById(productId);
        verify(customerBalanceService).validateCustomerBalance(customer, discountedPrice);

        // Ensure other methods are not called
        verifyNoMoreInteractions(customerDiscountService, productService, customerService, productInventoryService, orderProductRepositorySave);
    }


    @Test
    void testInsufficientQuantityException() {
        Long customerId = 1L;
        Long productId = 2L;
        BigDecimal discountedPrice = BigDecimal.valueOf(900);

        // Mock responses
        DiscountProductResponse discountProductResponse = new DiscountProductResponse();
        discountProductResponse.setDiscountedPrice(discountedPrice);
        when(customerDiscountService.getDiscountedProductResponse(customerId)).thenReturn(discountProductResponse);

        Customer customer = new Customer();
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);

        Product product = new Product();
        product.setId(productId);
        product.setQuantity(1); // Устанавливаем доступное количество продукта
        product.setName("iphone 15");
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Mock product quantities
        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product, 2); // Пытаемся заказать два продукта, хотя доступен только один

        // Mock validation: throw InsufficientQuantityException
        doThrow(new InsufficientQuantityException("Insufficient quantity"))
                .when(productInventoryService).validateProductQuantities(anyMap()); // Используем anyMap(), чтобы не специфицировать конкретный объект

        // Test
        assertThrows(InsufficientQuantityException.class, () -> {
            orderService.makeOrderForDiscountedProduct(customerId, productId);
        });

        // Verify interactions
        verify(customerDiscountService).getDiscountedProductResponse(customerId);
        verify(customerFinderService).findCustomerById(customerId);
        verify(productFinderService).findProductById(productId);
        verify(customerBalanceService).validateCustomerBalance(customer, discountedPrice);
        verify(productInventoryService).validateProductQuantities(anyMap()); // Проверяем вызов метода с любыми аргументами

        // Ensure other methods are not called
        verifyNoMoreInteractions(customerDiscountService, productService, customerService, productInventoryService, orderProductRepositorySave);
    }
}










































