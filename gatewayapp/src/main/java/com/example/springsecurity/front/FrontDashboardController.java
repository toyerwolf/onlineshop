package com.example.springsecurity.front;

import com.example.springsecurity.dto.CustomerRegistrationsByYearResponseDTO;
import com.example.springsecurity.dto.YearlySalesResponseDto;
import com.example.springsecurity.dto.YearlySalesRevenueResponseDTO;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FrontDashboardController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private DashboardGraphComponent dashboardGraphComponent;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Получение данных о продажах по годам
        YearlySalesResponseDto yearlySalesResponseDto = productService.getProductSalesStatistics();
        List<Object> productSalesData = dashboardGraphComponent.getProductSalesData(yearlySalesResponseDto);
        model.addAttribute("productSalesData", productSalesData);

        // Получение данных о доходах по годам
        YearlySalesRevenueResponseDTO yearlySalesRevenueResponseDTO = productService.getTotalProductSalesRevenueByYear();
        List<Object> salesRevenueData = dashboardGraphComponent.getSalesRevenueData(yearlySalesRevenueResponseDTO);
        model.addAttribute("salesRevenueData", salesRevenueData);

        // Получение данных о регистрациях клиентов по годам
        CustomerRegistrationsByYearResponseDTO customerRegistrationsByYearResponseDTO = customerService.getCustomerRegistrationsByYear();
        List<Object> customerRegistrationsData = dashboardGraphComponent.getCustomerRegistrationsData(customerRegistrationsByYearResponseDTO);
        model.addAttribute("customerRegistrationsData", customerRegistrationsData);

        return "dashboardtest";
    }
}