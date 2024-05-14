package com.example.springsecurity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "order_test")

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("isPaid")
    private boolean isPaid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonProperty("orderProducts")
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

    public void addProduct(Product product, int quantity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(quantity);
        orderProduct.setOrder(this);
        orderProducts.add(orderProduct);
    }


//    @ManyToMany
//    @JoinTable(
//            name = "order_product",
//            joinColumns = @JoinColumn(name = "order_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    private Set<Product> products = new HashSet<>();







}
