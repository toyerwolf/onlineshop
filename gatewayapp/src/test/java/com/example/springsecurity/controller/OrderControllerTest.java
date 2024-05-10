//package com.example.springsecurity.controller;
//
//import com.example.springsecurity.entity.Order;
//import com.example.springsecurity.repository.ProductRepository;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.CustomerService;
//import com.example.springsecurity.service.OrderService;
//import lombok.AllArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles(profiles = "integration")
//@EnableConfigurationProperties
//@EnableAutoConfiguration
//@AllArgsConstructor
//class OrderControllerTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @LocalServerPort
//    private int port;
//    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE1MTk1MDU2LCJleHAiOjE3MTUxOTg2NTZ9.g0YXs6CoswX0NlnuFSETP18tc9XBA_P13zaYLJ3a24M";
//
//
//
//    private OrderService orderService;
//
//
//    private CustomerService customerService;
//
//
//
//
//    private ProductRepository productRepository;
//
//
//    @Sql(scripts = {
//            "classpath:sql/customerainsert.sql",
//            "classpath:sql/orderinsert.sql",
//            "classpath:sql/userinsert.sql"
//    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Test
//    public void testMakeOrder() {
//        // Prepare test data
//        Long customerId = 1L;
//        Long cardId = 100L;
//        OrderRequest orderRequest = new OrderRequest();
//        Map<Long, Integer> productQuantities = new HashMap<>();
//        productQuantities.put(101L, 2); // Example product ID and quantity
//        orderRequest.setProductQuantities(productQuantities);
//
//        Order mockedOrder = new Order(); // Create a mocked Order object
//        // Mock the behavior of orderService.makeOrderWithCard() method
//        when(orderService.makeOrder(any(Long.class), any(OrderRequest.class), any(Long.class))).thenReturn(mockedOrder);
//
//        // Prepare headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//
//        // Prepare request entity
//        HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);
//
//        // Perform the POST request to the controller endpoint
//        ResponseEntity<Order> responseEntity = restTemplate.exchange(
//                "http://localhost:" + port + "/" + customerId + "/" + cardId,
//                HttpMethod.POST,
//                requestEntity,
//                Order.class);
//
//        // Verify the response status code
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//}}
//
