package com.example.springsecurity.controller;

import com.example.springsecurity.dto.DiceRollResult;
import com.example.springsecurity.dto.ProductDto;
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
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DiceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    private String userToken;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @BeforeEach
    void setUp() {
        userToken = "Bearer " + jwtTestUtil.generateToken(2L, "USER");

    }

    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void applyDiscountIfDiceRollsAreSix() {
        // Given
        long customerId = 2L;
        long productId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<DiceRollResult> responseEntity = restTemplate.exchange(
                createURLWithPort("/api/roll-dice?customerId=" + customerId + "&productId=" + productId),
                HttpMethod.POST,
                entity,
                DiceRollResult.class);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        DiceRollResult rollResult = responseEntity.getBody();

    }






    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}