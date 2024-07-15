package com.example.springsecurity.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}