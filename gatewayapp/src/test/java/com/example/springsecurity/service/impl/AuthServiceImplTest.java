package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.security.JwtTokenProvider;
import com.example.springsecurity.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;



    @Test
    void testLogin_Success() {

        String username = "testUser";
        String password = "testPassword";
        String token = "testToken";
        String refreshToken = "testRefreshToken";

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setUsername("username");
        userPrincipal.setPassword("password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, password);

        JwtResponse jwtResponse = new JwtResponse(token, refreshToken);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(authentication);


        when(jwtTokenProvider.generateTokens(userPrincipal)).thenReturn(jwtResponse);


        JwtResponse response = authService.login(loginDto);


        assertEquals(token, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());

    }

    @Test
    void testRefreshAccessTokenAndGenerateNewToken_ValidOldToken() {
        // Arrange
        String validToken = "valid_token";
        String username = "username";
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUserNameFromJwtToken(validToken)).thenReturn(username);
        when(jwtTokenProvider.generateTokenFromUsername(username)).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshTokenFromUsername(username)).thenReturn("new_refresh_token");



        // Act
        JwtResponse jwtResponse = authService.refreshAccessTokenAndGenerateNewToken(validToken);

        // Assert
        assertNotNull(jwtResponse);
        assertEquals("new_access_token", jwtResponse.getToken());
        assertEquals("new_refresh_token", jwtResponse.getRefreshToken());
        verify(jwtTokenProvider).generateTokenFromUsername(username);
        verify(jwtTokenProvider).generateRefreshTokenFromUsername(username);
    }

    @Test
    void testRefreshAccessTokenAndGenerateNewToken_InvalidOldToken() {
        String invalidToken = "invalid_token";
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authService.refreshAccessTokenAndGenerateNewToken(invalidToken));
        verify(jwtTokenProvider, never()).getUserNameFromJwtToken(anyString());
        verify(jwtTokenProvider, never()).generateTokenFromUsername(anyString());
        verify(jwtTokenProvider, never()).generateRefreshTokenFromUsername(anyString());
    }


}





