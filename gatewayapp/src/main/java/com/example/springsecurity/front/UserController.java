package com.example.springsecurity.front;

import com.example.springsecurity.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<Long> getUserId() {
        // Получение аутентификации из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверка аутентификации и типа Principal
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            Long userId = userPrincipal.getId(); // Получаем userId из UserPrincipal
            return ResponseEntity.ok(userId);
        } else {
            // В случае, если Principal не UserPrincipal, возвращаем ошибку или пустой ответ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}