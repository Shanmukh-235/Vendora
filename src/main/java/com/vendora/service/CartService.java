package com.vendora.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.vendora.model.CartItem;
import com.vendora.model.Product;
import com.vendora.model.User;

@Service
@SessionScope // ✅ Each user gets their own cart (isolated session)
public class CartService {

    // Maps productId -> quantity
    private final Map<Long, Integer> cartItems = new ConcurrentHashMap<>();

    // Maps productId -> Product snapshot
    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    /** 🛒 Add or update product in the cart */
    public void addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) return;
        productMap.put(product.getId(), product);
        cartItems.merge(product.getId(), quantity, Integer::sum);
    }

    /** ❌ Remove product completely */
    public void removeFromCart(Long productId) {
        cartItems.remove(productId);
        productMap.remove(productId);
    }

    /** 🧼 Clear all cart items */
    public void clearCart() {
        cartItems.clear();
        productMap.clear();
    }

    /** 🧾 Get current cart items (unmodifiable copy) */
    public Map<Long, Integer> getCartItems() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(cartItems));
    }

    public int getCartItemsCount(User user) {
        return cartItems.size();
    }

    /** 💰 Calculate total price */
    public BigDecimal getTotal() {
        return cartItems.entrySet().stream()
                .map(e -> {
                    Product p = productMap.get(e.getKey());
                    if (p != null && p.getPrice() != null) {
                        return p.getPrice().multiply(BigDecimal.valueOf(e.getValue()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 🧩 Convert cart map into a list of CartItem objects */
    public List<CartItem> getCartItemsList() {
        List<CartItem> list = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = productMap.get(entry.getKey());
            if (product != null) {
                CartItem item = new CartItem();
                item.setProduct(product);
                item.setQuantity(entry.getValue());
                list.add(item);
            }
        }
        return list;
    }

    /** 🔽 Decrease quantity by 1 or remove if 0 */
    public void decreaseQuantity(Long productId) {
        if (productId == null || !cartItems.containsKey(productId)) return;
        int current = cartItems.get(productId);
        if (current <= 1) removeFromCart(productId);
        else cartItems.put(productId, current - 1);
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
