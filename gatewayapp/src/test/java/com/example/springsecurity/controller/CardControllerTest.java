package com.example.springsecurity.controller;

import com.example.springsecurity.req.CustomerCardReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
class CardControllerTest {

    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate restTemplate;

    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzE2MzUyMDk5LCJleHAiOjE3MTYzNTU2OTl9.Br2-Mt6AjOdrjHQC0FLqCReNdibNVNWYMl-Suerez7k";


    @Test
    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/userinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "huseyn1",roles = {"USER"})
    public void testAddCardToCustomer() {
        long customerId=2L;
        CustomerCardReq customerCardReq = new CustomerCardReq();
        customerCardReq.setCardNumber("1234567890123456");
        customerCardReq.setCardBalance(BigDecimal.valueOf(1000.0));
        customerCardReq.setCvv("123");
        customerCardReq.setExpirationDate(LocalDate.now().plusYears(1));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", token);
        //HttpEnityt нужен для передаввния headers или для тела запроса
        HttpEntity<CustomerCardReq> request = new HttpEntity<>(customerCardReq, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                createURLWithPort("/cards/" + customerId),
                request,
                String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Card added successfully", responseEntity.getBody());
    }





    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}