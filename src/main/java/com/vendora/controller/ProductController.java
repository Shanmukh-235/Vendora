package com.vendora.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vendora.model.Category;
import com.vendora.model.Product;
import com.vendora.repository.CategoryRepository;
import com.vendora.repository.ProductRepository;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Show all products or by category
    @GetMapping
    public String listProducts(@RequestParam(value = "category", required = false) Long categoryId, Model model) {
    List<Product> products;
    if (categoryId != null) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        products = category != null
                ? productRepository.findByCategory(category.getName()) // use String name
                : productRepository.findAll();
    } else {
        products = productRepository.findAll();
    }

    model.addAttribute("products", products);
    model.addAttribute("categories", categoryRepository.findAll());
    model.addAttribute("selectedCategory", categoryId);
    return "user/products";
}
}
