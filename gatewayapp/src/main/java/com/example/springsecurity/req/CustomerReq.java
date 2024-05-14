package com.example.springsecurity.req;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReq {

    private Long id;

    @NotNull
    @Size(min = 3,max = 25)
    private String name;

    @NotNull
    @Size(min = 3,max = 25)
    private String surname;

    @PositiveOrZero
    private BigDecimal balance;
    private String address;
}
