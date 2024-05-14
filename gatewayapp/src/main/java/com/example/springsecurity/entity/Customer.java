package com.example.springsecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Customer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private BigDecimal balance;
    private String address;
    private LocalDateTime registeredAt;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
//    @JsonIgnore
    private User user;


//    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orderList;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerCardDetails> cards = new ArrayList<>();

    public void addCard(CustomerCardDetails card) {
        cards.add(card);
        card.setCustomer(this);
    }


}


