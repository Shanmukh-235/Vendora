package com.vendora.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vendora.model.Product;
import com.vendora.repository.ProductRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Fetch top 4 or 8 featured products (based on what you prefer)
        List<Product> featuredProducts = productRepository.findTop4ByOrderByIdDesc();
        model.addAttribute("products", featuredProducts);
        return "index"; // the Thymeleaf home page
    }
}
