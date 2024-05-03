package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDto toDto(Customer customer);
    List<CustomerDto> toDtoList(List<Customer> customers);

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDto customerDto);
}
