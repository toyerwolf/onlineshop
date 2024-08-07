package com.example.springsecurity.controller;
import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.OrderProductsResponse;
import com.example.springsecurity.security.JwtTestUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String userToken;


    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    void setUp() {
        userToken = "Bearer " + jwtTestUtil.generateToken(2L, "USER");

    }

    @Test
    @Sql(scripts = {"classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/order-product.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testFindOrderProductsByOrderId() {
        long orderId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<OrderProductsResponse> response = restTemplate.exchange(
                createURLWithPort("/order-products/" + orderId + "/products"),
                HttpMethod.GET,
                httpEntity,
                OrderProductsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getProducts().isEmpty());
        assertEquals(2, response.getBody().getProducts().size());

        OrderProductDto firstProduct = response.getBody().getProducts().get(0);
        OrderProductDto secondProduct = response.getBody().getProducts().get(1);

        assertEquals(1L, firstProduct.getOrderId());
        assertEquals("samsung", firstProduct.getProductName());
        assertEquals(5, firstProduct.getQuantity());

        assertEquals(2L, secondProduct.getOrderId());
        assertEquals("test", secondProduct.getProductName());
        assertEquals(3, secondProduct.getQuantity());
    }





    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}