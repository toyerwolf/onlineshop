package com.example.springsecurity.controller;

import com.example.springsecurity.req.UserRegistrationReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RegistrationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRegisterUser() {
        String registerUrl = createURLWithPort("/users");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UserRegistrationReq userRegistrationReq = new UserRegistrationReq();
        userRegistrationReq.setUsername("test_user");
        userRegistrationReq.setPassword("test_password");
        userRegistrationReq.setName("Test");
        userRegistrationReq.setSurname("User");
        userRegistrationReq.setAddress("123 Test Street");
        userRegistrationReq.setBalance(BigDecimal.ZERO);

        HttpEntity<UserRegistrationReq> requestEntity = new HttpEntity<>(userRegistrationReq, headers);


        ResponseEntity<String> responseEntity = restTemplate.exchange(registerUrl, HttpMethod.POST, requestEntity, String.class);


        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("registered", responseEntity.getBody());
    }




    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }


}