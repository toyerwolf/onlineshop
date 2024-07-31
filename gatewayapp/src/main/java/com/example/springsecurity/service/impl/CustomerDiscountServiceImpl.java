package com.example.springsecurity.service.impl;


import com.example.springsecurity.dto.DiceRollResult;
import com.example.springsecurity.dto.DiscountProductResponse;
import com.example.springsecurity.entity.CustomerDiscount;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.DiceRollAlreadyPerformedException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerDiscountRepository;
import com.example.springsecurity.service.CustomerDiscountService;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CustomerDiscountServiceImpl implements CustomerDiscountService {

    private static final String TARGET_PRODUCT_NAME = "iPhone 15";
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.10); // 10% discount
    private static final int TARGET_DICE_VALUE = 6;
    private static final int DICE_SIDES = 6;
    private final ProductFinderService productFinderService;

    private final CustomerDiscountRepository customerDiscountRepository;
    private final ProductService productService;
    private final CustomerService customerService;
    private final CustomerFinderService customerFinderService;
    @Transactional
    public DiceRollResult applyDiscountIfDiceRollsAreSix(Long customerId, Long productId) {
        CustomerDiscount customerDiscount = getOrCreateCustomerDiscount(customerId, productId);
        if (customerDiscount.isDiceRolled()) {
            throw new DiceRollAlreadyPerformedException("Dice roll already performed for this customer and product.");
        }
        int die1 = rollDice();
        int die2 = rollDice();
        customerDiscount.setDie1(die1);
        customerDiscount.setDie2(die2);
        customerDiscount.setDiceRolled(true);

        if (die1 == TARGET_DICE_VALUE && die2 == TARGET_DICE_VALUE) {
            applyDiscountIfApplicable(customerDiscount, productId);
        }

        customerDiscountRepository.save(customerDiscount);
        return new DiceRollResult(die1, die2, customerDiscount.isDiceRolled());
    }





    @Override
    @Transactional
    public DiscountProductResponse getDiscountedProductResponse(Long customerId) {
        CustomerDiscount customerDiscount = customerDiscountRepository.findFirstByCustomerIdAndDiscountPriceNotNull(customerId);
        if (customerDiscount == null) {
            throw new NotFoundException("No discounted product found for customer with id: " + customerId);
        }
        Product product = customerDiscount.getProduct();
        BigDecimal discountPrice = customerDiscount.getDiscountPrice();
        BigDecimal discount = customerDiscount.getDiscount();
        String imageName = product.getImageUrl();
        String imageUrl = "/static/" + imageName;
        return new DiscountProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                discountPrice,
                discount,
                imageUrl
        );
    }
    public CustomerDiscount getOrCreateCustomerDiscount(Long customerId, Long productId) {
        CustomerDiscount customerDiscount = customerDiscountRepository.findByCustomerIdAndProductId(customerId, productId);

        if (customerDiscount == null) {
            customerDiscount = new CustomerDiscount();
            customerDiscount.setCustomer(customerFinderService.findCustomerById(customerId));
            customerDiscount.setProduct(productFinderService.findProductById(productId));
        }
        return customerDiscount;
    }

    int rollDice() {
        return (int) (Math.random() * DICE_SIDES) + 1;
    }



    public void applyDiscountIfApplicable(CustomerDiscount customerDiscount, Long productId) {
        Product product = productFinderService.findProductById(productId);
        if (TARGET_PRODUCT_NAME.equals(product.getName())) {
            customerDiscount.setDiscount(DISCOUNT_PERCENTAGE);
            customerDiscount.setDiscountPrice(calculateDiscountPrice(product.getPrice()));
            customerDiscount.setExpiresAt(LocalDateTime.now().plusDays(7));
        }
    }

    public BigDecimal calculateDiscountPrice(BigDecimal originalPrice) {
        BigDecimal discountAmount = originalPrice.multiply(CustomerDiscountServiceImpl.DISCOUNT_PERCENTAGE);
        return originalPrice.subtract(discountAmount);
    }


}

