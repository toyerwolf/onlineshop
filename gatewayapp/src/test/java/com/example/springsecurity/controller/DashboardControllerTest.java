package com.example.springsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
class DashboardControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE1OTQ0Njc3LCJleHAiOjE3MTU5NDgyNzd9.b0taYYuqhxCgWQYtYkstpQI0l6-XrK9X9H29FJtY9J8";

    @Test
    @Sql(scripts = {
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/order-product.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProductSalesStatistics() {
        int year = 2023;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(
                createURLWithPort("/dashboard/products/total-sold?year=" + year),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Integer> salesByYear = response.getBody();
        assertNotNull(salesByYear);
        assertFalse(salesByYear.isEmpty());
        assertTrue(salesByYear.containsKey("samsung"));
        assertEquals(Integer.valueOf(8), salesByYear.get("samsung"));
        assertTrue(salesByYear.containsKey("test"));
        assertEquals(Integer.valueOf(7), salesByYear.get("test"));


        for (Map.Entry<String, Integer> entry : salesByYear.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }


    @Test
    @Sql(scripts = {
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/order-product.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProductSalesStatistics_AdminRole_Success() {
        // Arrange
        int expectedYear = 2023;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<Integer, Integer>> response = restTemplate.exchange(
                createURLWithPort("/dashboard/product-sales-statistic"),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<Integer, Integer> salesByYear = response.getBody();
        assertNotNull(salesByYear);
        assertFalse(salesByYear.isEmpty());
        assertTrue(salesByYear.containsKey(expectedYear));
        // Добавьте здесь дополнительные проверки для убеждения в правильности данных
    }






    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}