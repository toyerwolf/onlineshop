package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerCardDetailsRepository;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.req.CustomerCardReq;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.CardService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final CustomerRepository customerRepository;
   private final CustomerCardDetailsRepository cardDetailsRepository;
   private final CustomerFinderService customerFinderService;
   private final AuthService authService;


    public void addCardToCustomer(CustomerCardReq customerCardReq, Long customerId) {
        // Поиск клиента по полученному customerId
        Customer customer = customerFinderService.findCustomerById(customerId);

        if (isCardNumberUnique(customerCardReq.getCardNumber())) {
            CustomerCardDetails cardDetails = new CustomerCardDetails();
            cardDetails.setCardNumber(customerCardReq.getCardNumber());
            cardDetails.setExpirationDate(customerCardReq.getExpirationDate());
            cardDetails.setCvv(customerCardReq.getCvv());
            cardDetails.setCardBalance(customerCardReq.getCardBalance());
            customer.addCard(cardDetails);
            customerRepository.save(customer);
        } else {
            throw new AlreadyExistException("Card with this number already exists");
        }
    }

    public void validateCardBalance(CustomerCardDetails card, BigDecimal totalAmount) {
        BigDecimal cardBalance = card.getCardBalance();
        if (cardBalance.compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance on card");
        }
    }

    public boolean isCardExpired(LocalDate expirationDate) {
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(expirationDate);
    }

    boolean isCardNumberUnique(String cardNumber) {
        Optional<CustomerCardDetails> existingCard = cardDetailsRepository.findByCardNumber(cardNumber);
        return existingCard.isEmpty();
    }

    @Transactional
    public void decreaseFromCardBalance(CustomerCardDetails card, BigDecimal amount) {
        BigDecimal currentBalance = card.getCardBalance();

        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds on the card");
        }
        BigDecimal newBalance = currentBalance.subtract(amount);
        card.setCardBalance(newBalance);
        cardDetailsRepository.save(card);
    }



}

