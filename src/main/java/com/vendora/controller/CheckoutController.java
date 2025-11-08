package com.vendora.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.CartItem;
import com.vendora.model.Order;
import com.vendora.model.User;
import com.vendora.service.CartService;
import com.vendora.service.OrderService;
import com.vendora.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    /** 🧾 Display Checkout Page */
    @GetMapping
    public String checkoutPage(Model model, Principal principal) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> items = cartService.getCartItemsList();
        if (items.isEmpty()) {
            model.addAttribute("error", "Your cart is empty!");
            return "shop/cart";
        }

        BigDecimal total = cartService.getTotal();

        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "shop/checkout";
    }

    /** 💳 Place Order */
    @PostMapping
    public String placeOrder(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to complete your order.");
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItemsList();
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/cart";
        }

        // ✅ Create and save order
        Order order = orderService.placeOrder(user, cartItems);

        // ✅ Clear cart AFTER placing order
        cartService.clearCart();

        redirectAttributes.addFlashAttribute("success", "✅ Order placed successfully!");
        return "redirect:/user/orders";
    }

    /** 🧾 Order History */
    @GetMapping("/history")
    public String orderHistory(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("orders", orderService.getOrdersByUser(user));
        }
        return "user/orders";
    }
}
