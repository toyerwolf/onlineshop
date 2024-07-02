package com.example.springsecurity.service;

import com.example.springsecurity.dto.DiceRollResult;
import com.example.springsecurity.dto.DiscountProductResponse;

public interface CustomerDiscountService {


    DiceRollResult applyDiscountIfDiceRollsAreSix(Long customerId, Long productId);

    DiscountProductResponse getDiscountedProductResponse(Long customerId);
}
