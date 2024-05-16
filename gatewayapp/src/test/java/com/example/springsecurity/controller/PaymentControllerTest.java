package com.example.springsecurity.controller;

import com.example.springsecurity.req.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration

class PaymentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE1ODM5NzY3LCJleHAiOjE3MTU4NDMzNjd9.VlOUGt9phn-pFvm886xO5yzP-_gwKnYaX-zcbxYwJQs";

    @Test
    @Sql(scripts = {"classpath:sql/customerainsert.sql",
            "classpath:sql/orderinsert.sql",
            "classpath:sql/userinsert.sql",
              "classpath:sql/card-add.sql"  })

    void processPayment_ValidRequest_ReturnsCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setCustomerId(2L);
        paymentRequest.setCardId(1L);
        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/payments"),
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order with card created", response.getBody());
    }

    @Test
    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/orderinsert.sql",
            "classpath:sql/userinsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void processPaymentWithPayPal_ValidRequest_ReturnsOk() {
        long customerId = 2L;
        long orderId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/payments/paypal/" + customerId + "/" + orderId),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment with PayPal processed successfully.", response.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}



