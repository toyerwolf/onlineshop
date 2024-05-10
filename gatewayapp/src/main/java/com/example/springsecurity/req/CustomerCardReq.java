package com.example.springsecurity.req;
import com.example.springsecurity.validation.FutureOrPresentLocalDateTime;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class CustomerCardReq {



    @NotBlank
    @Pattern(regexp = "^\\d{16}$", message = "Invalid card number format")
    private String cardNumber;

//    @FutureOrPresentLocalDateTime
    @FutureOrPresent
    private LocalDate expirationDate;

    @NotBlank
    @Pattern(regexp = "^\\d{3}$", message = "Invalid CVV format")
    private String cvv;

    @PositiveOrZero
    private BigDecimal cardBalance;

}
