package com.example.springsecurity.controller;

import com.example.springsecurity.req.CustomerCardReq;
import com.example.springsecurity.security.JwtTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CardControllerTest {

    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @Test
    @Sql(scripts = {"classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAddCardToCustomer() {
        // Генерация токена для клиента с ID 2
        String token = "Bearer " + jwtTestUtil.generateToken(2L, "USER");
        System.out.println("Generated Token: " + token);

        CustomerCardReq customerCardReq = new CustomerCardReq();
        customerCardReq.setCardNumber("1234567890123456");
        customerCardReq.setCardBalance(BigDecimal.valueOf(1000.0));
        customerCardReq.setCvv("123");
        customerCardReq.setExpirationDate(LocalDate.now().plusYears(1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<CustomerCardReq> request = new HttpEntity<>(customerCardReq, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                createURLWithPort("/cards"),
                request,
                String.class);

        // Отладочная информация
        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
        System.out.println("Response Body: " + responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Card added successfully", responseEntity.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

