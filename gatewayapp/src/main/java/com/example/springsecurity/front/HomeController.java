package com.example.springsecurity.front;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.req.UserRegistrationReq;
import com.example.springsecurity.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {


    private final AuthService authService;
    private final RegistrationService registrationService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public String home(Model model, @RequestParam(defaultValue = "1") int page, HttpSession session) {
        int pageSize = 3;
        Page<ProductDto> products = productService.getAllProducts(page, pageSize);
        List<CategoryDto> categories = categoryService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        Long customerId = (Long) session.getAttribute("customerId");
        if (customerId != null) {
            CustomerDto customer = customerService.getCustomerById(customerId);
            model.addAttribute("customer", customer);
        }
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

        // Получаем пользователя по username
        CustomerDto customer = customerService.getCustomerByUsername(loginDto.getUsername());
        session.setAttribute("customerId", customer.getId());

        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegistrationReq request, Model model) {
        registrationService.register(request);
        model.addAttribute("message", "Пользователь успешно зарегистрирован");
        return "redirect:/login";
    }



    @GetMapping("home/{categoryId}/products")
    @ResponseBody
    public List<ProductDto> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.findProductsByCategoryId(categoryId);
    }

    @GetMapping("home/categories")
    @ResponseBody
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("home/search")
    @ResponseBody
    public List<ProductDto> searchProductByName(@RequestParam String keyword) {
        return productService.searchProductByName(keyword);
    }

    @PostMapping("home/makeOrder")
    @ResponseBody
    public OrderResponse makeOrder(HttpSession session, @RequestBody OrderRequest orderRequest) {
        // Получаем ID клиента из сеанса
        Long customerId = (Long) session.getAttribute("customerId");

        // Проверяем, получен ли ID клиента из сеанса
        if (customerId == null) {
            // Если ID клиента не доступен, вернуть сообщение об ошибке
            throw new NotFoundException("Ошибка: идентификатор клиента не найден в сеансе.");
        }

        // Вызываем сервис создания заказа, передавая ID клиента и информацию о заказе
        return orderService.makeOrder(customerId, orderRequest);
    }

    @PostMapping("home/makeOrderWithCard")
    @ResponseBody
    public OrderResponse makeOrderWithCard(@RequestParam Long customerId, @RequestBody OrderRequest orderRequest, @RequestParam Long cardId) {
        return orderService.makeOrderWithCard(customerId, orderRequest, cardId);
    }
}

