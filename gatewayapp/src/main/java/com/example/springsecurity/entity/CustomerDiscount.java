package com.example.springsecurity.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "customer_discount",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "product_id"})
)
public class CustomerDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal discount;

    private BigDecimal discountPrice; // Скидочная цена для конкретного клиента

    private LocalDateTime expiresAt;

    private boolean diceRolled;

    private int die1; // Результат первого броска кости

    private int die2; // Результат второго броска кости

   // Флаг, указывающий, были ли уже брошены кости

    // Геттеры, сеттеры и другие методы, как у вас в текущем коде
}