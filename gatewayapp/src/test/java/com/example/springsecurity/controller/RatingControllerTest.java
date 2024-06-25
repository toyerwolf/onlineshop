package com.example.springsecurity.controller;

import com.example.springsecurity.dto.RatingDto;
import com.example.springsecurity.entity.Rating;
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
import org.unbescape.html.HtmlEscape;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingControllerTest {


    @Autowired
    private JwtTestUtil jwtTestUtil;

    String userToken;

    @BeforeEach
    void setUp() {
        userToken = "Bearer " + jwtTestUtil.generateToken(2L, "USER");

    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(scripts = {
            "classpath:sql/category-add.sql",
            "classpath:sql/productadd.sql",
            "classpath:sql/userinsert.sql",
            "classpath:sql/customerainsert.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addRatingToProduct(){
        Long productId = 1L;
        RatingDto ratingDto=new RatingDto();
        ratingDto.setProductId(productId);
        ratingDto.setRating(4);
        ratingDto.setCustomerId(2L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", userToken);

        HttpEntity<RatingDto> httpEntity=new HttpEntity<>(ratingDto,headers);

        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                createURLWithPort("/ratings/{id}/rate"), HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
                }, productId);

        // Проверяем ответ
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertTrue(Objects.requireNonNull(responseEntity.getBody()).containsKey("message"));
        assertEquals("Thank you for your rating!", responseEntity.getBody().get("message"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }





}

