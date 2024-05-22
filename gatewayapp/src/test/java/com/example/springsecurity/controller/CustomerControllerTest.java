package com.example.springsecurity.controller;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.security.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.w3c.dom.ls.LSInput;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String userToken;
    private String adminToken;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    void setUp() {
        userToken = "Bearer " + jwtTestUtil.generateToken(2L, "USER");
        adminToken = "Bearer " + jwtTestUtil.generateToken(1L, "ADMIN");
    }

    @LocalServerPort
    private int port;


    @Test
    @Sql(scripts = {
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetCustomerById() {
        long customerId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<CustomerDto> response = restTemplate.exchange(
                createURLWithPort("/customers/" + customerId),
                HttpMethod.GET,
                request,
                CustomerDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
    }

    @Test
    @Sql(scripts = {
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"

    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAllCustomers() {
        int pageNumber = 1;
        int pageSize = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", adminToken);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<CustomerDto>> responseEntity = restTemplate.exchange(
                createURLWithPort("/customers?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<CustomerDto> customerDtoList = responseEntity.getBody();
        assertNotNull(customerDtoList);
        assertEquals(pageSize, customerDtoList.size());
    }




    @Test
    @Sql(scripts = {"classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql",

    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testSearchCustomers() {
    String keyword="John";
    HttpHeaders headers=new HttpHeaders();
    headers.set("Authorization",adminToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Void> httpEntity=new HttpEntity<>(headers);

    ResponseEntity<List<CustomerDto>> response=restTemplate.exchange
            (createURLWithPort("/customers//searching?keyword=" + keyword),
                    HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
                    });

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<CustomerDto> customerDtos = response.getBody();
        assertNotNull(customerDtos);
        assertFalse(customerDtos.isEmpty());
        assertTrue(customerDtos.stream().anyMatch(dto -> dto.getName().contains("John")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql",
            "classpath:sql/orderinsert.sql",
    },executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetOrdersByCustomerId() {
        Long customerId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",userToken);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                createURLWithPort("/customers/" + customerId + "/orders"),
                HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
                });


        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<OrderDto> orderDtos = response.getBody();
        assertNotNull(orderDtos);
        assertFalse(orderDtos.isEmpty());
        assertTrue(orderDtos.stream().allMatch(dto -> dto.getCustomerId().equals(customerId)));


}



    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}