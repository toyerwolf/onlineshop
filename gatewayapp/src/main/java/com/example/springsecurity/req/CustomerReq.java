package com.example.springsecurity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReq {

    private Long id;
    private String name;
    private String surname;
    private BigDecimal balance;
    private String address;
}
