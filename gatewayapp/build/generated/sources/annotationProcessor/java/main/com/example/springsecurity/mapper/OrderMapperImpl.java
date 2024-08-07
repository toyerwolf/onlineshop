package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Order;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T14:50:41+0400",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto orderToOrderDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setCustomerId( orderCustomerId( order ) );
        orderDto.setUpdatedAt( order.getUpdatedAt() );
        orderDto.setId( order.getId() );
        orderDto.setTotalAmount( order.getTotalAmount() );
        orderDto.setCreatedAt( order.getCreatedAt() );
        orderDto.setStatus( order.getStatus() );

        return orderDto;
    }

    @Override
    public List<OrderDto> ordersToOrderDtos(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderDto> list = new ArrayList<OrderDto>( orders.size() );
        for ( Order order : orders ) {
            list.add( orderToOrderDto( order ) );
        }

        return list;
    }

    private Long orderCustomerId(Order order) {
        if ( order == null ) {
            return null;
        }
        Customer customer = order.getCustomer();
        if ( customer == null ) {
            return null;
        }
        Long id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
