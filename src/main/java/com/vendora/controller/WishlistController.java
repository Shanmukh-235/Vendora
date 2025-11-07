package com.vendora.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.model.Wishlist;
import com.vendora.service.CartService;
import com.vendora.service.ProductService;
import com.vendora.service.UserService;
import com.vendora.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/shop/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    /** ❤️ View all wishlist items */
    @GetMapping
    public String viewWishlist(Model model) {
        User user = userService.getLoggedInUser();
        if (user == null) return "redirect:/login";

        List<Wishlist> wishlist = wishlistService.getUserWishlist(user);

        // Initialize products to avoid lazy-loading issues
        wishlist.forEach(w -> {
            if (w.getProduct() != null) w.getProduct().getId();
        });

        List<Product> wishlistProducts = wishlist.stream()
                .map(Wishlist::getProduct)
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("wishlist", wishlistProducts);
        model.addAttribute("user", user);
        return "shop/wishlist";
    }

    /** ➕ Add product to wishlist */
    @GetMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to manage your wishlist.");
            return "redirect:/login";
        }

        Product product = productService.findById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/shop";
        }

        if (wishlistService.isInWishlist(user, product)) {
            redirectAttributes.addFlashAttribute("info", product.getName() + " is already in your wishlist ❤️");
        } else {
            wishlistService.addToWishlist(user, product);
            redirectAttributes.addFlashAttribute("success", product.getName() + " ❤️ added to your wishlist!");
        }

        return "redirect:/shop/product/" + productId;
    }

    /** ❌ Remove product from wishlist */
    @GetMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser();
        if (user == null) return "redirect:/login";

        Product product = productService.findById(productId);
        if (product != null) {
            wishlistService.removeFromWishlist(user, product);
            redirectAttributes.addFlashAttribute("success", product.getName() + " removed from wishlist 🗑️");
        }
        return "redirect:/shop/wishlist";
    }

    /** 🛒 Move product from wishlist to cart */
    @GetMapping("/move-to-cart/{productId}")
    public String moveWishlistItemToCart(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to manage your wishlist.");
            return "redirect:/login";
        }

        Product product = productService.findById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/shop/wishlist";
        }

        // Add to cart
        cartService.addToCart(product, 1);
        // Remove from wishlist
        wishlistService.removeFromWishlist(user, product);

        redirectAttributes.addFlashAttribute("success", product.getName() + " moved to your cart 🛒");
        return "redirect:/shop/wishlist";
    }
}
