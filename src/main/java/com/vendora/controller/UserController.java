package com.vendora.controller;

import java.awt.Color;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vendora.model.Order;
import com.vendora.model.OrderItem;
import com.vendora.model.User;
import com.vendora.service.CartService;
import com.vendora.service.OrderService;
import com.vendora.service.UserService;
import com.vendora.service.WishlistService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------- DASHBOARD ----------------
    // ---------------- DASHBOARD ----------------
    @GetMapping("/user/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // ✅ Get stats
        long orderCount = orderService.countOrdersByUser(user);
        int wishlistCount = wishlistService.getWishlistItemsCount(user);
        int cartCount = cartService.getCartItemsCount(user);

        // ✅ Fetch only the most recent order (1)
        List<Order> orders = orderService.getOrdersByUser(user).stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())) // newest first
                .limit(1)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("wishlistCount", wishlistCount);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("orders", orders);

        return "user/dashboard";
    }

    // ---------------- ORDERS ----------------
    @GetMapping("/user/orders")
    public String viewOrders(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    // ---------------- SETTINGS ----------------
    @GetMapping("/user/settings")
    public String userSettings(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "user/settings";
    }

    // ---------------- PROFILE UPDATE ----------------
    @PostMapping("/user/update-profile")
    public String updateProfile(@RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);

        // Password update
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.equals(confirmPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
            } else {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/user/settings";
            }
        }

        // Profile image
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String uploadDir = "uploads/profiles/";
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }

                String fileName = user.getId() + "_" + profileImage.getOriginalFilename();
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                java.nio.file.Files.write(filePath, profileImage.getBytes());
                user.setProfileImage("/uploads/profiles/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Image upload failed. Try again!");
                return "redirect:/user/settings";
            }
        }

        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/user/settings";
    }

    @GetMapping("/user/orders/cancel/{orderId}")
    public String cancelOrder(@PathVariable Long orderId, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderId);
        if (order != null && "PLACED".equalsIgnoreCase(order.getStatus())) {
            orderService.cancelOrderAndRestoreStock(order);
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully and stock restored.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Order cannot be cancelled.");
        }

        return "redirect:/user/orders";
    }

    @GetMapping("/user/orders/view/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // ✅ Fetch the order safely
        Order order = orderService.getOrderById(id);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/orders";
        }

        // ✅ Fetch the products in the order
        List<OrderItem> orderItems = orderService.getOrderItemsByOrder(order);

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("items", orderItems);

        return "user/order-details"; // ➜ templates/user/order-details.html
    }

    @GetMapping("/user/orders/invoice/{id}")
    public void downloadInvoice(@PathVariable Long id, Principal principal, HttpServletResponse response)
            throws IOException {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        Order order = orderService.getOrderById(id);
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            response.sendRedirect("/user/orders");
            return;
        }

        // 🚫 Prevent invoice for cancelled orders
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            response.sendRedirect("/user/orders");
            return;
        }

        List<OrderItem> items = orderService.getOrderItemsByOrder(order);

        // ✅ Prepare PDF response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Vendora_Invoice_" + order.getId() + ".pdf");

        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // ---------- Header ----------
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLUE);
        Paragraph title = new Paragraph("Vendora Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice No: INV-" + order.getId()));
        document.add(new Paragraph("Order Date: " +
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm").format(order.getCreatedAt())));
        document.add(new Paragraph("Customer: " + user.getName()));
        document.add(new Paragraph("Email: " + user.getEmail()));
        document.add(new Paragraph("Address: " + (user.getAddress() != null ? user.getAddress() : "Not Provided")));
        document.add(new Paragraph(" "));

        // ---------- Table ----------
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[] { 3f, 1f, 1.5f, 1.5f });

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Stream.of("Product", "Qty", "Price", "Subtotal").forEach(col -> {
            PdfPCell header = new PdfPCell(new Phrase(col, headFont));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setBackgroundColor(Color.LIGHT_GRAY);
            header.setPadding(6f);
            table.addCell(header);
        });

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (OrderItem item : items) {
            table.addCell(new Phrase(item.getProduct().getName(), cellFont));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), cellFont));
            table.addCell(new Phrase("₹" + item.getPrice(), cellFont));
            table.addCell(new Phrase("₹" + item.getPrice()
                    .multiply(java.math.BigDecimal.valueOf(item.getQuantity().longValue())), cellFont));
        }

        document.add(table);

        // ---------- Total ----------
        document.add(new Paragraph(" "));
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Paragraph total = new Paragraph("Total Amount: ₹" + order.getTotalAmount(), totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Payment Method: Cash on Delivery", FontFactory.getFont(FontFactory.HELVETICA, 11)));
        document.add(
                new Paragraph("Order Status: " + order.getStatus(), FontFactory.getFont(FontFactory.HELVETICA, 11)));

        // ---------- Footer ----------
        document.add(new Paragraph(" "));
        Paragraph thank = new Paragraph("Thank you for shopping with Vendora!",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Color.GRAY));
        thank.setAlignment(Element.ALIGN_CENTER);
        document.add(thank);

        document.close();
    }
}
