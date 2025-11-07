package com.vendora.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.service.CartService;
import com.vendora.service.ProductService;
import com.vendora.service.UserService;

@Controller
@RequestMapping("/shop/action")
public class ShopActionController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // 🛒 Add to Cart
    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {

        // ✅ Get logged-in user
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to add items to your cart.");
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/login";
        }

        Product product = productService.findById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/shop";
        }

        cartService.addToCart(user, product, quantity);
        redirectAttributes.addFlashAttribute("success", product.getName() + " added to your cart!");
        return "redirect:/shop/product/" + id;
    }

    // ⚡ Buy Now (Single Product Checkout)
    @GetMapping("/buy-now/{id}")
    public String buyNow(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to make a purchase.");
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/login";
        }

        Product product = productService.findById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/shop";
        }

        cartService.clearCart(user);
        cartService.addToCart(user, product, 1);
        redirectAttributes.addFlashAttribute("info", "Proceeding to checkout...");
        return "redirect:/shop/checkout";
    }
}
