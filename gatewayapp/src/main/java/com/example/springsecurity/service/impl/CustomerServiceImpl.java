package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CustomerDto;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.CustomerMapper;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper=CustomerMapper.INSTANCE;

    @Override
    public void decreaseBalance(Long customerId, BigDecimal amount) {
        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new NotFoundException("Customer not found"));
        BigDecimal currentBalance = customer.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        BigDecimal newBalance = currentBalance.subtract(amount);
        customer.setBalance(newBalance);
        customerRepository.save(customer);

    }

    @Override
    public CustomerDto getCustomerById(Long customerID) {
        Customer customer=customerRepository.findById(customerID).orElseThrow(()->new NotFoundException("Customer not found"));
        return CustomerMapper.INSTANCE.toDto(customer);
    }


    @Override
    public Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Override
    public Page<CustomerDto> getAllCustomer(int pageNumber, int pageSize) {
        Pageable pageable= PageRequest.of(pageNumber-1,pageSize);
        Page<Customer> customers=customerRepository.findAll(pageable);
        List<CustomerDto> customerDtos = customerMapper.toDtoList(customers.getContent());
        return new PageImpl<>(customerDtos, pageable, customers.getTotalElements());
    }


    //berem dannie iz repository
    public Map<Integer, Long> getCustomerRegistrationsByYear() {
        //poluchayem dannie registracii customer-ov
        List<Object[]> registrationData = customerRepository.getCustomerRegistrationsByYear();
        Map<Integer, Long> customerRegistrationsByYear = new HashMap<>();

        //proxodimsa po kajdoy stroke dannix o registracii
        for (Object[] row : registrationData) {
            //izvlekayem qod i kolichestvo i pomeshayem v customerRegistrationsByYear
            int year = ((Number) row[0]).intValue();
            long customerRegistrations = ((Number) row[1]).longValue();
            customerRegistrationsByYear.put(year, customerRegistrations);
        }
        return customerRegistrationsByYear;
    }

    @Override
    public List<CustomerDto> searchCustomers(String keyword) {
        List<Customer> customers=customerRepository.searchCustomers(keyword);
        return customers.stream()
                .map(CustomerMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
}
