package com.example.springsecurity.controller;

import com.example.springsecurity.dto.JwtRequest;
import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.example.springsecurity.security","com.example.springsecurity.config"})
class AuthControllerTest {


    @LocalServerPort
    private int port;


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


//    @Autowired
//    private PasswordEncoder passwordEncoder;




    @Sql(scripts = {
            "classpath:sql/customerainsert.sql",
            "classpath:sql/userinsert.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

    @Test
    public void testLogin() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("huseyn");
        loginDto.setPassword("Password1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity(
                createURLWithPort("/auth/login"),
                request,
                JwtResponse.class);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


        JwtResponse jwtResponse = responseEntity.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getToken());
        assertNotNull(jwtResponse.getRefreshToken());

        String token = jwtResponse.getToken();
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testRefreshToken() {

        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE1ODM1NDQzLCJleHAiOjE3MTU4MzkwNDN9.23JTZrjUeMCAODjZ7fwUVNrnxvNK6FZDySaPkfJT52w");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JwtRequest> request = new HttpEntity<>(jwtRequest, headers);
        ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity(
                createURLWithPort("/auth/refresh"),
                request,
                JwtResponse.class);

        // Проверка успешности обновления токена
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JwtResponse jwtResponse = responseEntity.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getToken());
        assertNotNull(jwtResponse.getRefreshToken());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}