package com.example.springsecurity.req;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @Valid
    private Map<Long, @Positive Integer> productQuantities;


}
