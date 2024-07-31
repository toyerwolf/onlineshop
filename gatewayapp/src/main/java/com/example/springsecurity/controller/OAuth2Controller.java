//package com.example.springsecurity.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.reactive.result.view.RedirectView;
//
//@Controller
//public class OAuth2Controller {
//
//    @GetMapping("/oauth2/authorization/google")
//    public RedirectView googleLogin(HttpServletRequest request, HttpServletResponse response) {
//        // Если требуется, можно добавить дополнительную логику здесь
//        return new RedirectView("/oauth2/authorization/google");
//    }
//
//    @GetMapping("/login/oauth2/code/google")
//    public RedirectView handleGoogleLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        // Дополнительная логика для обработки успешного входа через Google
//        // Например, сохранение информации о пользователе или создание сессии
//        // ...
//
//        // Перенаправление на главную страницу после успешного входа
//        return new RedirectView("/");
//    }
//}
//
