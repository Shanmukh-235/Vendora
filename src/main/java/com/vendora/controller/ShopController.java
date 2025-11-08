package com.vendora.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.CartItem;
import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.service.CartService;
import com.vendora.service.OrderService;
import com.vendora.service.ProductService;
import com.vendora.service.UserService;
import com.vendora.service.WishlistService;

@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private WishlistService wishlistService;

    // 🛍️ All Products
    @GetMapping
    public String viewShop(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "category", required = false) String category,
                           @RequestParam(value = "sort", required = false) String sort,
                           Model model) {

        List<Product> products = productService.getAllProducts();

        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        if (category != null && !category.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        if ("priceAsc".equals(sort)) {
            products = products.stream().sorted(Comparator.comparing(Product::getPrice)).toList();
        } else if ("priceDesc".equals(sort)) {
            products = products.stream().sorted(Comparator.comparing(Product::getPrice).reversed()).toList();
        } else if ("newest".equals(sort)) {
            products = products.stream().sorted(Comparator.comparing(Product::getCreatedAt).reversed()).toList();
        }

        List<String> categories = productService.getAllProducts().stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        User user = userService.getLoggedInUser();

        model.addAttribute("user", user);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSort", sort);

        return "shop/shop";
    }

    // 🧾 Product Details
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) return "redirect:/shop";

        List<Product> relatedProducts = productService.findByCategory(product.getCategory()).stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .toList();

        User user = userService.getLoggedInUser();
        boolean isInWishlist = (user != null) && wishlistService.isInWishlist(user, product);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("user", user);
        model.addAttribute("isInWishlist", isInWishlist);

        return "shop/product-details";
    }

    // 🛒 Cart
    @GetMapping("/cart")
    public String viewCart(Model model) {
        Map<Long, Integer> cartItems = cartService.getCartItems();

        List<Product> products = cartItems.keySet().stream()
                .map(productService::findById)
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("products", products);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getTotal());
        return "shop/cart";
    }

    // 🧾 Checkout Page
    @GetMapping("/checkout")
    public String checkout(Model model) {
        Map<Long, Integer> cartItems = cartService.getCartItems();

        List<Product> products = cartItems.keySet().stream()
                .map(productService::findById)
                .filter(Objects::nonNull)
                .toList();

        BigDecimal total = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(cartItems.getOrDefault(p.getId(), 0))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("products", products);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("user", userService.getLoggedInUser());

        return "shop/checkout";
    }

    // ✅ Confirm Checkout — Creates Order
    @PostMapping("/checkout/confirm")
    public String confirmCheckout(Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser();

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to complete your order.");
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCartItemsList();
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/shop/cart";
        }

        orderService.placeOrder(user, cartItems);
        cartService.clearCart();

        redirectAttributes.addFlashAttribute("success", "✅ Order placed successfully!");
        return "redirect:/user/orders";
    }
}
