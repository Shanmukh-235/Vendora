package com.vendora.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.model.Wishlist;
import com.vendora.repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public List<Wishlist> getWishlistItemsByUser(User user) {
        return wishlistRepository.findByUser(user);
    }

    @Transactional
    public void addToWishlist(User user, Product product) {
        try {
            // ✅ Safer duplicate prevention
            if (wishlistRepository.existsByUserAndProduct(user, product)) {
                return;
            }

            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);

        } catch (DataIntegrityViolationException e) {
            // ✅ Gracefully handle if MySQL still blocks duplicate insert
            System.err.println("⚠️ Wishlist duplicate suppressed for user " + user.getId() + " product " + product.getId());
        }
    }

    public void removeFromWishlist(User user, Product product) {
        Wishlist existing = wishlistRepository.findByUserAndProduct(user, product);
        if (existing != null) {
            wishlistRepository.delete(existing);
        }
    }

    public List<Wishlist> getUserWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    public boolean isInWishlist(User user, Product product) {
        return wishlistRepository.existsByUserAndProduct(user, product);
    }

    public int getWishlistItemsCount(User user) {
        if (user == null) return 0;
        return getWishlistItemsByUser(user).size();
    }

}
