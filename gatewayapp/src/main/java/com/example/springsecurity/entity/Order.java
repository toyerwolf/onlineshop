package com.example.springsecurity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "order_test")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean isPaid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts=new ArrayList<>();


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    //metod dlya polucheniya produktov ix iz kolichestva
    public Map<Product, Integer> getProductQuantities() {
        Map<Product, Integer> productQuantities = new HashMap<>();
        for (OrderProduct orderItem : orderProducts) {
            Product product = orderItem.getProduct();
            Integer quantity = orderItem.getQuantity();
            productQuantities.put(product, quantity);
        }
        return productQuantities;
    }


//    @ManyToMany
//    @JoinTable(
//            name = "order_product",
//            joinColumns = @JoinColumn(name = "order_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    private Set<Product> products = new HashSet<>();







}
