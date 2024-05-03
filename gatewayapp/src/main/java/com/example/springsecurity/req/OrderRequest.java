package com.example.springsecurity.req;


import lombok.Data;


import java.util.Map;


@Data
public class OrderRequest {
//    private Long customerId;
    private Map<Long, Integer> productQuantities;

//    private List<OrderLineItem> lineItems;
}
