package com.vendora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.model.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Wishlist findByUserAndProduct(User user, Product product);
    List<Wishlist> findByUser(User user);
    boolean existsByUserAndProduct(User user, Product product);
}
