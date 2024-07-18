package com.example.springsecurity.front;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.req.UserRegistrationReq;
import com.example.springsecurity.security.JwtTokenProvider;
import com.example.springsecurity.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final OrderProductService orderProductService;
    private final PaymentService paymentService;

    @GetMapping
    public String home(Model model, @RequestParam(defaultValue = "1") int page, HttpSession session) {
        int pageSize = 9;
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
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto) {
        JwtResponse jwtResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
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




    @GetMapping("/home/{categoryId}/products")
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

    @Secured("USER")
    @PostMapping("home/makeOrder")
    public ResponseEntity<OrderResponse> makeOrder(@RequestHeader("Authorization") String authHeader, @RequestBody OrderRequest orderRequest) {
        String token = authHeader.replace("Bearer ", "");
        Long customerId = jwtTokenProvider.getUserIdFromJWT(token);

        // Вызываем сервис создания заказа, передавая ID клиента и информацию о заказе
        OrderResponse orderResponse = orderService.makeOrder(customerId, orderRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("home/makeOrderWithCard")
    @ResponseBody
    public OrderResponse makeOrderWithCard(@RequestParam Long customerId, @RequestBody OrderRequest orderRequest, @RequestParam Long cardId) {
        return orderService.makeOrderWithCard(customerId, orderRequest, cardId);
    }

    @GetMapping("/user/me")
    public ResponseEntity<CustomerDto> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long customerId = jwtTokenProvider.getUserIdFromJWT(token);
        CustomerDto customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("home/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody JwtRequest jwtRequest) {
        JwtResponse jwtResponse = authService.refreshAccessTokenAndGenerateNewToken(jwtRequest.getRefreshToken());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @GetMapping("/order-details")
    public String getOrderDetailsPage() {
        return "order-details";
    }



    @Secured("USER")
    @GetMapping("/test/{orderId}/products")
    public ResponseEntity<List<OrderProductDto>> getOrderProducts(@PathVariable Long orderId) {
        List<OrderProductDto> orderProducts = orderProductService.findOrderProductsByOrderId(orderId);
        return new ResponseEntity<>(orderProducts, HttpStatus.OK);
    }

    @Secured("USER")
    @PostMapping("/paypal/{customerId}/{orderId}")
    public ResponseEntity<String> processPaymentWithPayPal(@PathVariable Long customerId, @PathVariable Long orderId) {
        paymentService.processPaymentWithPayPal(customerId, orderId);
        return ResponseEntity.ok("Payment with PayPal processed successfully.");
    }

    @GetMapping("/payment-success")
    public String showPaymentSuccessPage() {
        return "payment-success";
    }

    @GetMapping("/customer-order.html")
    public String showCustomerOrdersPage() {
        return "customer-order";
    }


    @GetMapping("/product-details")
    public String getProductDetailsPage() {
        return "product-details";
    }

    @GetMapping("home/products/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        model.addAttribute("product", productDto);
        return "product-details";
    }

    @GetMapping("/wishlist.html")
    public String viewWishlist() {
        return "wishlist"; // возвращает имя файла wishlist.html
    }

    @GetMapping("/currency.html")
    public String showCurrencyPage() {
        return "currency"; // Это имя представления (view name), которое соответствует currency.html
    }




}
