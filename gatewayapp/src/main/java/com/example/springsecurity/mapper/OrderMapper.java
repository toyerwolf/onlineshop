package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "updatedAt", target = "updatedAt")
    OrderDto orderToOrderDto(Order order);

    List<OrderDto> ordersToOrderDtos(List<Order> orders);
}
