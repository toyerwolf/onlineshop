package com.example.springsecurity.req;

import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data

public class PaymentRequest {

    private Long orderId;
    private Long customerId;
    private Long cardId;
}
