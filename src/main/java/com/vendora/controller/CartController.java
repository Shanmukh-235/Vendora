package com.vendora.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vendora.model.Product;
import com.vendora.service.CartService;
import com.vendora.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    /** 🛒 View Cart Page */
    @GetMapping
    public String viewCart(Model model) {
        Map<Long, Integer> cartItems = cartService.getCartItems();
        List<Product> products = cartItems.keySet().stream()
                .map(productService::findById)
                .filter(p -> p != null)
                .toList();

        BigDecimal total = cartService.getTotal();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("products", products);
        model.addAttribute("total", total);

        return "shop/cart";
    }

    /** ➕ Add to Cart */
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity) {
        Product product = productService.findById(productId);
        if (product != null) {
            cartService.addToCart(product, quantity);
        }
        return "redirect:/cart";
    }

    /** 🔼 Increase Quantity */
    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId) {
        Product product = productService.findById(productId);
        if (product != null) {
            cartService.addToCart(product, 1);
        }
        return "redirect:/cart";
    }

    /** 🔽 Decrease Quantity */
    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId) {
        cartService.decreaseQuantity(productId);
        return "redirect:/cart";
    }

    /** ❌ Remove Product */
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    /** 🧹 Clear Cart */
    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
