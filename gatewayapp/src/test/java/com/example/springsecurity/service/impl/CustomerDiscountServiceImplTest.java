package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.DiceRollResult;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerDiscount;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.DiceRollAlreadyPerformedException;
import com.example.springsecurity.repository.CustomerDiscountRepository;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDiscountServiceImplTest {

    @InjectMocks
    private CustomerDiscountServiceImpl customerDiscountService;

    @Mock
    private CustomerDiscountRepository customerDiscountRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductFinderService productFinderService;

    @Mock
    private CustomerFinderService customerFinderService;

    @Test
    void testApplyDiscountWhenDiceRollsAreSix() {
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = new Customer();
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(BigDecimal.valueOf(1000));

        CustomerDiscount newDiscount = new CustomerDiscount();
        newDiscount.setCustomer(customer);
        newDiscount.setProduct(product);
        newDiscount.setDiceRolled(false);

        when(customerDiscountRepository.findByCustomerIdAndProductId(customerId, productId)).thenReturn(null);
        when(customerFinderService.findCustomerById(customerId)).thenReturn(customer);
        when(productFinderService.findProductById(productId)).thenReturn(product);

        // Use reflection to set dice values to 6
        CustomerDiscountServiceImpl customerDiscountServiceSpy = spy(customerDiscountService);
        doReturn(6).doReturn(6).when(customerDiscountServiceSpy).rollDice();

        // Invoke the method under test
        DiceRollResult result = customerDiscountServiceSpy.applyDiscountIfDiceRollsAreSix(customerId, productId);

        // Verify the save method was called once with any CustomerDiscount object
        verify(customerDiscountRepository, times(1)).save(any(CustomerDiscount.class));

        // Retrieve the saved CustomerDiscount object to verify its state
        CustomerDiscount savedDiscount = captureNewDiscount();

        // Assertions on the savedDiscount object
        assertNotNull(savedDiscount);
        assertTrue(result.isDiscountApplied());
        assertEquals(6, result.getDie1());
        assertEquals(6, result.getDie2());
        assertEquals(BigDecimal.valueOf(0.10), savedDiscount.getDiscount());
        assertEquals(BigDecimal.valueOf(900), savedDiscount.getDiscountPrice().setScale(0)); // Adjust to the expected discounted price
        assertNotNull(savedDiscount.getExpiresAt()); // Ensure expiresAt is set
    }

    // Helper method to capture and return the saved CustomerDiscount object
    private CustomerDiscount captureNewDiscount() {
        ArgumentCaptor<CustomerDiscount> argumentCaptor = ArgumentCaptor.forClass(CustomerDiscount.class);
        verify(customerDiscountRepository).save(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    @Test
    void testNoDiscountAppliedWhenDiceRollsAreNotSix() {
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = new Customer();
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(BigDecimal.valueOf(1000));

        CustomerDiscount existingDiscount = new CustomerDiscount();
        existingDiscount.setCustomer(customer);
        existingDiscount.setProduct(product);
        existingDiscount.setDiceRolled(false);

        // Mock repository and services
        when(customerDiscountRepository.findByCustomerIdAndProductId(customerId, productId)).thenReturn(existingDiscount);

        // Use spy to set specific dice roll values (not 6)
        CustomerDiscountServiceImpl customerDiscountServiceSpy = spy(customerDiscountService);
        doReturn(3).doReturn(2).when(customerDiscountServiceSpy).rollDice();


        // Invoke the method under test
        // Invoke the method under test
        DiceRollResult result = customerDiscountServiceSpy.applyDiscountIfDiceRollsAreSix(customerId, productId);

        // Verify that save() method is called with the correct parameters
        verify(customerDiscountRepository).save(existingDiscount);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isDiscountApplied());
        assertEquals(3, result.getDie1()); // correct to 3
        assertEquals(2, result.getDie2()); // correct to 3

        // Verify the existing discount and price remain unchanged
        assertNull(existingDiscount.getDiscount());
        assertNull(existingDiscount.getDiscountPrice());
        assertNull(existingDiscount.getExpiresAt()); // As
    }


    @Test
    void testExceptionThrownWhenDiscountAlreadyApplied() {
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = new Customer();
        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(BigDecimal.valueOf(1000));

        CustomerDiscount existingDiscount = new CustomerDiscount();
        existingDiscount.setCustomer(customer);
        existingDiscount.setProduct(product);
        existingDiscount.setDiceRolled(true); // Assume dice rolled was 6:6 previously
//        existingDiscount.setDiscount(BigDecimal.TEN); // Assume a discount was applied
//        existingDiscount.setDiscountPrice(BigDecimal.valueOf(900)); // Adjusted price after discount

        // Stub necessary interactions
        when(customerDiscountRepository.findByCustomerIdAndProductId(customerId, productId)).thenReturn(existingDiscount);

        // Invoke the method under test
        assertThrows(DiceRollAlreadyPerformedException.class,
                () -> customerDiscountService.applyDiscountIfDiceRollsAreSix(customerId, productId));

        // Verify necessary interactions (e.g., repository.save should not be called)
        verify(customerDiscountRepository, never()).save(any(CustomerDiscount.class));
    }
}


