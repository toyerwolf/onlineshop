package com.example.springsecurity.controller;


import com.example.springsecurity.dto.ChangePasswordDto;
import com.example.springsecurity.req.UserReq;
import com.example.springsecurity.securiy.JwtTokenProvider;
import com.example.springsecurity.service.PasswordService;
import com.example.springsecurity.service.RegistrationService;
import com.example.springsecurity.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("auth")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegistrationService registrationService;
    private final PasswordService passwordService;
    private final UserService userService;


    @PostMapping("login")
    public ResponseEntity<String> login(
            @ModelAttribute UserReq userReq){
        Authentication authentication=authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(userReq.getUsername(),userReq.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(token);
    }


    @PostMapping("registration")
    public String performRegistration(@RequestBody @Valid UserReq userReq){
        registrationService.register(userReq);
        return "created";
    }

    @PostMapping("changePassword")
    public ResponseEntity<String> changePassword(@ModelAttribute ChangePasswordDto changePasswordDto){
        return new ResponseEntity<>(passwordService.changePassword(changePasswordDto), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully");

















}}
