package com.example.springsecurity.controller;





import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.dto.OrderResponse;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.OrderRepository;

import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.impl.ProductInventoryService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.example.springsecurity.service.impl"})
class OrderControllerTest {



    @LocalServerPort
    private int port;

    private String userToken;
    private String adminToken;
    @BeforeEach
    void setUp() {

        userToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzE2MzU2MTc0LCJleHAiOjE3MTYzNTk3NzR9.r-dJEFmGU0ACx_HOYzpZ0olUBnwBjUDCC5aAQnkKdnw";
        adminToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE2MzU2MTU2LCJleHAiOjE3MTYzNTk3NTZ9.NkANt0Pwcpd5fUtW5VvofGcBWOhkpCGrSL0w87Nq26o";
    }





    @Autowired
    private TestRestTemplate restTemplate;

    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testMakeOrder_Success() {
        long customerId = 2L;
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 1);
        productQuantities.put(2L, 1);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);
        HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<OrderResponse> response = restTemplate.exchange(
                createURLWithPort("/orders/" + customerId),
                HttpMethod.POST,
                requestEntity,
                OrderResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        OrderResponse orderResponse = response.getBody();
        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getId());
        assertNotNull(orderResponse.getTotalAmount());
        assertNotNull(orderResponse.getCreatedAt());
        assertNotNull(orderResponse.getStatus());
    }

    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/card-add.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testMakeOrderWithCard() {
        long customerId = 2L;
        long cardId = 1L;
        Map<Long, Integer> productQuantities = new HashMap<>();
        productQuantities.put(1L, 1);
        productQuantities.put(2L, 1);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductQuantities(productQuantities);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", userToken);
        HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<OrderResponse> response = restTemplate.exchange(
                createURLWithPort("/orders/" + customerId + "/" + cardId),
                HttpMethod.POST,
                requestEntity,
                OrderResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrderResponse orderResponse = response.getBody();
        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getId());
        assertNotNull(orderResponse.getTotalAmount());
        assertNotNull(orderResponse.getCreatedAt());
        assertNotNull(orderResponse.getStatus());

    }

    @Test
    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/orderinsert.sql",
            "classpath:sql/userinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testMarkOrderAsDelivered(){
        long orderId=1L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);
        HttpEntity<Void> httpEntity=new HttpEntity<>(headers);

        ResponseEntity<String> response=restTemplate.exchange(createURLWithPort("/orders/"+orderId+"/"+"deliver"),
                HttpMethod.PATCH,
                httpEntity,String.class);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Order marked as delivered successfully.",response.getBody());
    }

    @Test
    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/orderinsert.sql",
            "classpath:sql/userinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAllOrders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List<OrderDto>> responseEntity = restTemplate.exchange(
                createURLWithPort("/orders"),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


        List<OrderDto> orders = responseEntity.getBody();
        assertNotNull(orders);

    }






    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }



}



