package com.example.springsecurity.mapper;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Customer.CustomerBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T14:50:41+0400",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerDto toDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();

        customerDto.setId( customer.getId() );
        customerDto.setName( customer.getName() );
        customerDto.setSurname( customer.getSurname() );
        customerDto.setBalance( customer.getBalance() );
        customerDto.setAddress( customer.getAddress() );
        customerDto.setRegisteredAt( customer.getRegisteredAt() );

        return customerDto;
    }

    @Override
    public List<CustomerDto> toDtoList(List<Customer> customers) {
        if ( customers == null ) {
            return null;
        }

        List<CustomerDto> list = new ArrayList<CustomerDto>( customers.size() );
        for ( Customer customer : customers ) {
            list.add( toDto( customer ) );
        }

        return list;
    }

    @Override
    public Customer toEntity(CustomerDto customerDto) {
        if ( customerDto == null ) {
            return null;
        }

        CustomerBuilder customer = Customer.builder();

        customer.name( customerDto.getName() );
        customer.surname( customerDto.getSurname() );
        customer.balance( customerDto.getBalance() );
        customer.address( customerDto.getAddress() );
        customer.registeredAt( customerDto.getRegisteredAt() );

        return customer.build();
    }
}
