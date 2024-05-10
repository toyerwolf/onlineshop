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
    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE1MTgyNDg1LCJleHAiOjE3MTUxODYwODV9.bEvR3M1lbzqyBocXgKMNWfdBN02MM7LStTxfWtO8O5k";

    @Test
    void processPayment_ValidRequest_ReturnsCreated() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(123L);
        paymentRequest.setCustomerId(456L);
        paymentRequest.setCardId(789L);
        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/payments"),
                HttpMethod.POST,
                requestEntity,
                Void.class
        );
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



