package com.example.springsecurity.repository;

import com.example.springsecurity.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListItemRepository extends JpaRepository<WishlistItem,Long> {


    @Modifying
    @Query("DELETE FROM WishlistItem wi WHERE wi.wishlist.customer.id = :customerId AND wi.product.id = :productId")
    void removeProductFromWishlist(Long customerId, Long productId);
}
