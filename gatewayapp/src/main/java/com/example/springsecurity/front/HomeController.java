package com.example.springsecurity.front;

import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.req.UserRegistrationReq;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.ProductService;
import com.example.springsecurity.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class HomeController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final ProductService productService;

    @GetMapping
    public String home(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 3; // Количество продуктов на странице
        Page<ProductDto> products = productService.getAllProducts(page, pageSize);
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, Model model, HttpSession session) {
        JwtResponse jwtResponse = authService.login(loginDto);
        session.setAttribute("accessToken", jwtResponse.getToken());
        session.setAttribute("refreshToken", jwtResponse.getRefreshToken());
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationReq request, Model model) {
        registrationService.register(request);
        model.addAttribute("message", "User registered successfully");
        return "redirect:/login";
    }
}