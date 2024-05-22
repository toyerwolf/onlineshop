package com.example.springsecurity.controller;

import com.example.springsecurity.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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


    String adminToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzE2MzU2MzQwLCJleHAiOjE3MTYzNTk5NDB9.YcC068pFZ32r4JBqRuvUlYPANXxdVLdk4CoSM9hmKxQ";

    @Test
    @WithMockUser(username = "john", roles = {"ADMIN"})
    @Sql(scripts = {
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/order-product.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProductSalesStatisticsPerYear() {
        int year = 2023;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ProductSalesResponseDto> response = restTemplate.exchange(
                createURLWithPort("/dashboard/products/total-sold?year=" + year),
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductSalesResponseDto salesResponse = response.getBody();
        assertNotNull(salesResponse);
        List<ProductSoldCountDTO> salesByYear = salesResponse.getProductSoldCounts();
        assertNotNull(salesByYear);
        assertFalse(salesByYear.isEmpty());

        Map<String, Integer> expectedSales = new HashMap<>();
        expectedSales.put("samsung", 8);
        expectedSales.put("test", 7);

        for (ProductSoldCountDTO productSoldCountDTO : salesByYear) {
            String productName = productSoldCountDTO.getProductName();
            Integer soldCount = productSoldCountDTO.getSoldCount();
            assertNotNull(productName);
            assertNotNull(soldCount);
            if (expectedSales.containsKey(productName)) {
                assertEquals(expectedSales.get(productName).intValue(), soldCount.intValue());
            }
        }
    }

    @Test
    @WithMockUser(username = "john", roles = {"ADMIN"})
    @Sql(scripts = {
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/order-product.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetProductSalesStatistics() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<YearlySalesResponseDto> response = restTemplate.exchange(
                createURLWithPort("/dashboard/products/sales-statistics"),
                HttpMethod.GET, requestEntity, YearlySalesResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        YearlySalesResponseDto yearlySalesResponseDto = response.getBody();
        assertNotNull(yearlySalesResponseDto);
        List<YearlyProductSalesDTO> yearlySales = yearlySalesResponseDto.getYearlySales();
        assertNotNull(yearlySales);
        assertFalse(yearlySales.isEmpty());

        Map<Integer, Integer> expectedSales = new HashMap<>();
        expectedSales.put(2023, 15);

        int totalSoldFor2023 = 0;

        for (YearlyProductSalesDTO yearlyProductSalesDTO : yearlySales) {
            if (yearlyProductSalesDTO.getYear() == 2023) {
                totalSoldFor2023 += yearlyProductSalesDTO.getTotalSold();
            }
        }
        assertEquals(expectedSales.get(2023).intValue(), totalSoldFor2023);
    }



    @Test
    @WithMockUser(username = "john", roles = {"ADMIN"})
    @Sql(scripts = {
            "classpath:sql/v1/categoryinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
            "classpath:sql/v1/productsinsert.sql",
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/ordersinsert.sql",
            "classpath:sql/v1/order-product.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetTotalProductSalesRevenue() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<YearlySalesRevenueResponseDTO> response = restTemplate.exchange(
                createURLWithPort("/dashboard/totalRevenueByYear"),
                HttpMethod.GET, requestEntity, YearlySalesRevenueResponseDTO.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        YearlySalesRevenueResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        List<YearlySalesRevenueDTO> yearlySales = responseBody.getYearlySales();
        assertNotNull(yearlySales);
        BigDecimal totalSalesRevenue = BigDecimal.ZERO;
        for (YearlySalesRevenueDTO yearlySalesRevenueDTO : yearlySales) {
            totalSalesRevenue = totalSalesRevenue.add(yearlySalesRevenueDTO.getTotalRevenue());
        }
        assertEquals(new BigDecimal("1500.00"), totalSalesRevenue);
    }

    @Test
    @Sql(scripts = {
            "classpath:sql/v1/customersinsert.sql",
            "classpath:sql/v1/usersinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetCustomerRegistrationsByYear() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<CustomerRegistrationsByYearResponseDTO> response = restTemplate.exchange(
                createURLWithPort("/dashboard/customer/registrations"),
                HttpMethod.GET, httpEntity,
                CustomerRegistrationsByYearResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerRegistrationsByYearResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);

        List<CustomerRegistrationDTO> registrationsByYear = responseBody.getRegistrationsByYear();
        assertNotNull(registrationsByYear);
        assertFalse(registrationsByYear.isEmpty());

        Map<Integer, Long> expectedRegistrationsByYear = new HashMap<>();
        expectedRegistrationsByYear.put(2023, 2L);
        for (CustomerRegistrationDTO registrationDTO : registrationsByYear) {
            int year = registrationDTO.getYear();
            long registrations = registrationDTO.getRegistrations();
            assertTrue(expectedRegistrationsByYear.containsKey(year));
            assertEquals(expectedRegistrationsByYear.get(year), registrations);
        }
    }













    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}