package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CustomerDto;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
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
    public List<CustomerDto> getAllCustomer(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customerMapper.toDtoList(customers.getContent());
    }


    //berem dannie iz repository
    @Override
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


    @Override
    public CustomerCardDetails getCustomerCardById(Customer customer, Long cardId) {
        List<CustomerCardDetails> cards = customer.getCards();
        for (CustomerCardDetails card : cards) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        throw new NotFoundException("Card not found for customer");
    }
}
