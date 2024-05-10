package com.example.springsecurity.req;


import lombok.Data;


import java.util.Map;


@Data
public class OrderRequest {
    private Map<Long, Integer> productQuantities;


}
