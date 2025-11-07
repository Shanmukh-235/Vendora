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
@SessionScope
public class CartService {

    // Map of productId -> quantity
    private final Map<Long, Integer> cartItems = new ConcurrentHashMap<>();

    // Map of productId -> Product snapshot
    private final Map<Long, Product> productMap = new ConcurrentHashMap<>();

    /** 🛒 Add or update a product in the cart (incremental). */
    public void addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0)
            return;
        productMap.put(product.getId(), product);
        cartItems.merge(product.getId(), quantity, Integer::sum);
    }

    /** 🧹 Remove a product completely from the cart. */
    public void removeFromCart(Long productId) {
        cartItems.remove(productId);
        productMap.remove(productId);
    }

    /** 🧼 Clear all cart items. */
    public void clearCart() {
        cartItems.clear();
        productMap.clear();
    }

    /** 🧾 Get current cart items as a safe, read-only map. */
    public Map<Long, Integer> getCartItems() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(cartItems));
    }

    /** 🧮 Calculate total cost of cart. */
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

    /** Convert session cart into CartItem objects (for order creation). */
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

    /** Quick helper for empty check. */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // -------------------------------------------------------------------
    // ✅ Compatibility overloads for controllers that expect User parameter
    // -------------------------------------------------------------------

    public void addToCart(User user, Product product, int quantity) {
        addToCart(product, quantity);
    }

    public void removeFromCart(User user, Long productId) {
        removeFromCart(productId);
    }

    public void clearCart(User user) {
        clearCart();
    }

    public Map<Long, Integer> getCartItems(User user) {
        return getCartItems();
    }

    /** ✅ Attach user to each cart item before sending to OrderService */
    public List<CartItem> getCartItemsList(User user) {
        List<CartItem> items = getCartItemsList();
        if (user != null) {
            for (CartItem item : items) {
                item.setUser(user);
            }
        }
        return items;
    }

    public BigDecimal getTotal(User user) {
        return getTotal();
    }

    // 🔽 Decrease quantity of a product by 1
    public void decreaseQuantity(Long productId) {
        if (productId == null || !cartItems.containsKey(productId)) {
            return;
        }

        int currentQty = cartItems.get(productId);
        if (currentQty <= 1) {
            removeFromCart(productId);
        } else {
            cartItems.put(productId, currentQty - 1);
        }
    }


    public boolean isEmpty(User user) {
        return isEmpty();
    }
}
