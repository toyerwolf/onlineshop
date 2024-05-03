package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.mapper.ProductMapper;
import com.example.springsecurity.repository.OrderProductRepository;
import com.example.springsecurity.service.OrderProductService;
import com.example.springsecurity.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderProductServiceImpl implements OrderProductService {

    private final OrderProductRepository orderProductRepository;



    @Override
    public List<OrderProductDto> findOrderProductsByOrderId(Long orderId) {
        List<OrderProduct> orderProducts = orderProductRepository.findOrderProductsByOrderId(orderId);
        List<OrderProductDto> orderProductDtos = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            OrderProductDto orderProductDto = new OrderProductDto();
            orderProductDto.setId(orderProduct.getId());
            orderProductDto.setQuantity(orderProduct.getQuantity());
            orderProductDto.setProductName(orderProduct.getProductName());
            orderProductDtos.add(orderProductDto);
        }
        return orderProductDtos;
    }

}
